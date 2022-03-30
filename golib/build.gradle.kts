import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.build.gradle.tasks.MergeSourceSetFolders
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
  kotlin("multiplatform")
  id("com.android.library")
  `maven-publish`
}

println("IDEMODE: ${ProjectVersions.IDE_MODE}")
group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

val jniLibsDir = project.buildDir.resolve("jniLibs")


fun kipfsBuild(platform: String) =
  tasks.register<Exec>("kipfsDebug${platform.capitalize()}") {

    commandLine(rootProject.file("bin/build_kipfs.sh"), platform)

    inputs.files(rootProject.fileTree("go") {
      include("**/*.go")
    } + rootProject.file("bin/build_kipfs.sh"))

    //project.buildDir.resolve("native/$platform")
    /* outputs.files(project.fileTree(project.buildDir.resolve("native/$platform")) {
       include("libgokipfs.so")
       include("libgokipfs.h")
     })*/
    outputs.files(
      project.buildDir.resolve("native/$platform/libgokipfs.so"),
      project.buildDir.resolve("native/$platform/libgokipfs.h")
    )

    doLast {
      logger.info("finished building kipfs for $platform")
    }
  }


kotlin {

  fun KotlinNativeTarget.configureSharedLib() {
    val platform = name
    val kipfsBuild = kipfsBuild(platform)

    val jniDir = when (platform) {
      "android386" -> "x86"
      "androidAmd64" -> "x86_64"
      "androidArm" -> "armeabi-v7a"
      "androidArm64" -> "arm64-v8a"
      else -> null
    }

    binaries {
      sharedLib {
        baseName = "kipfs"
        kipfsBuild.get().outputs.also {
          println("KIPFSBUILD OUTPUTS $platform: $name: ${it.files.files}")
        }
        if (jniDir != null && buildType == NativeBuildType.DEBUG) {
          val copyTask = tasks.register<Copy>("copyToJniLibs${platform.capitalize()}") {
            from(linkTask.outputs)
            from(kipfsBuild.get().outputs)
            into(jniLibsDir.resolve(jniDir))
          }
          //created a jar of the shared library for use in android/jvm jni applications
          kipfsBuild.get().finalizedBy(copyTask)
        }
      }
    }


    compilations["main"].apply {

      //println("NATIVECOMPILATION: ${this.name} ${this.konanTarget.name}")
      if (!ProjectVersions.IDE_MODE) {
        defaultSourceSet {
          dependsOn(sourceSets.getByName(if (jniDir != null) "androidNativeMain" else "linuxMain"))
        }
      }

      if (jniDir == null) {
        cinterops.create("jni") {
          packageName("danbroid.kipfs.jni")
          val jdkIncludes = rootProject.file("jdk").resolve("include")

          includeDirs(mutableListOf<File>().apply {
            add(jdkIncludes)
            add(jdkIncludes.resolve("linux"))
            add(jdkIncludes.resolve("win32"))
            add(jdkIncludes.resolve("darwin"))
          })
          extraOpts("-verbose")
        }
      }

      cinterops.create("libkipfs") {

        //defFile(project.file("src/nativeInterop/cinterop/KIpfsGo.def"))
        tasks.getAt(interopProcessingTaskName).also {
          it.inputs.files(kipfsBuild.get().outputs)
          it.dependsOn(kipfsBuild)
        }

        includeDirs(
          project.buildDir.resolve("native/$platform"),
          rootProject.file("openssl/libs/$platform/include")
        )

        extraOpts(
          mutableListOf<String>().apply {
            add("-verbose")
            //-Lopenssl/libs/linuxArm64/lib -L./golib/build/native/linuxArm64
            add("-libraryPath")
            add("${rootProject.file("openssl/libs/$platform/lib")}")
            add("-libraryPath")
            add("${project.buildDir.resolve("native/$platform")}")
          }
        )
      }
    }
  }

  android()

  linuxX64("linuxAmd64")

  //ide mode is linuxX64 target only. Turned off by ./gradlew -Pkipfs.ideMode=false  see ./scripts/publishAll.sh
  if (!ProjectVersions.IDE_MODE) {


    val nativeMain by sourceSets.creating
    val linuxMain by sourceSets.creating {
      dependsOn(nativeMain)
    }

    val androidNativeMain by sourceSets.creating {
      dependsOn(nativeMain)
    }


    mingwX64("windowsAmd64")
    linuxArm32Hfp("linuxArm")
    linuxArm64("linuxArm64")
    androidNativeX86("android386")
    androidNativeX64("androidAmd64")
    androidNativeArm64("androidArm64")
    androidNativeArm32("androidArm")
  }


  targets.withType(KotlinNativeTarget::class).all {
    configureSharedLib()
  }

  jvm {
    compilations["main"].compileKotlinTaskProvider.dependsOn("linkDebugSharedLinuxAmd64")
  }

  sourceSets {

    commonMain {
      dependencies {
        implementation(AndroidUtils.logging)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val jni by creating

    val androidMain by getting {
      dependsOn(jni)
    }

    val jvmMain by getting {
      dependsOn(jni)
    }

    if (ProjectVersions.IDE_MODE) {
      val linuxAmd64Main by getting {
        kotlin.srcDirs("src/linuxMain/kotlin", "src/nativeMain/kotlin")
      }

      val linuxAmd64Test by getting {
        kotlin.srcDir("src/nativeTest/kotlin")
      }
    }


    val androidAndroidTest by getting {
      dependencies {
        implementation(AndroidX.test.coreKtx)
        implementation(AndroidX.test.rules)
        implementation(AndroidX.test.runner)
        implementation(AndroidX.test.ext.junitKtx)
      }
    }
  }
}

tasks.withType(KotlinJvmTest::class) {
  val platform = "linuxAmd64"
  dependsOn("linkDebugShared${platform.capitalize()}")
  jvmArgs(
    "-Djava.library.path=${
      File(
        project.buildDir,
        "bin/$platform/debugShared/"
      ).absolutePath
    }"
  )
}


android {
  compileSdk = ProjectVersions.SDK_VERSION
  namespace = "com.github.danbrough.kipfs"

  defaultConfig {
    minSdk = ProjectVersions.MIN_SDK_VERSION
    targetSdk = ProjectVersions.SDK_VERSION
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  sourceSets {
    named("main") {
      manifest.srcFile("src/androidMain/AndroidManifest.xml")
      res.srcDirs("src/androidMain/res")
      jniLibs.srcDirs(project.buildDir.resolve("jniLibs"))
    }
  }
}


publishing {

  publications {

    kotlin.targets.withType(KotlinNativeTarget::class) {
      binaries.matching {
        it is SharedLibrary && !it.linkTask.target.startsWith("android") &&
            it.buildType == NativeBuildType.RELEASE
      }.all {


        val publicationName = "jni-${target.name}"

        afterEvaluate {
          tasks.named(
            if (buildType == NativeBuildType.DEBUG)
              "mergeDebugJniLibFolders" else "mergeReleaseJniLibFolders"
          ).dependsOn(linkTaskProvider)
        }

        val jarTask = tasks.create("${publicationName}Jar", Jar::class) {
          from(linkTask)
          dependsOn(linkTask)
        }

        create<MavenPublication>(publicationName) {
          artifactId = publicationName
          artifact(jarTask)
        }
      }
    }
  }

  repositories {
    maven(ProjectVersions.MAVEN_REPO)
  }
}

afterEvaluate {
  //make connectedDebugAndroidTest tasks dependent on the jni shared library
  kotlin.targets.withType(KotlinNativeTarget::class).asMap.values.flatMap { it.binaries.toList() }
    .filterIsInstance<SharedLibrary>()
    .filter {
      it.buildType == NativeBuildType.DEBUG &&
          it.linkTask.target.startsWith("android")
    }
    .forEach {
      tasks.named("connectedDebugAndroidTest").dependsOn(it.linkTaskProvider)
    }
}