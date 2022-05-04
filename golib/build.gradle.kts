import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeHostTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.android.library")
  `maven-publish`
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

val jniLibsDir = project.buildDir.resolve("jniLibs")


android {
  compileSdk = ProjectVersions.SDK_VERSION
  namespace = ProjectVersions.GROUP_ID


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


fun kipfsBuild(platform: String) =
  tasks.register<Exec>("kipfsDebug${platform.capitalize()}") {
    environment("ANDROID_NDK_ROOT", android.ndkDirectory.absolutePath)
    environment("PLATFORM", platform)
    doLast {
      println("kipfs build finished for $platform")
    }

    commandLine(rootProject.file("bin/build_kipfs.sh"))

    inputs.files(rootProject.fileTree("go") {
      include("**/*.go")
      include("**/*.c")
      include("**/*.h")
    } + rootProject.file("bin/build_kipfs.sh"))

    outputs.files(
      project.buildDir.resolve("native/$platform/libgokipfs.so"),
      project.buildDir.resolve("native/$platform/libgokipfs.h")
    )

    doLast {
      logger.info("finished building kipfs for $platform")
    }

  }



kotlin {

  val nativeMain by sourceSets.creating{
    dependsOn(sourceSets.getByName("commonMain"))
  }

  val nativeTest by sourceSets.creating {
    dependsOn(sourceSets.getByName("commonTest"))
  }

  val linuxMain by sourceSets.creating {
    dependsOn(nativeMain)
  }

  val androidNativeMain by sourceSets.creating {
    dependsOn(nativeMain)
  }


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

        if (jniDir != null && buildType == NativeBuildType.DEBUG) {
          //copy android libs to jniLibs directory
          tasks.register<Copy>("copyToJniLibs${platform.capitalize()}") {
            from(linkTask.outputs)
            from(kipfsBuild.get().outputs)
            into(jniLibsDir.resolve(jniDir))
          }.also {
            linkTask.finalizedBy(it)
          }
          //created a jar of the shared library for use in android/jvm jni applications
        }
      }
    }

    compilations["test"].apply {
      println("CONFIGURING COMPILATION $name")
    }

    compilations["main"].apply {

      println("NATIVECOMPILATION: ${this.name} ${this.konanTarget.name}")
      defaultSourceSet {
        dependsOn(if (jniDir != null) androidNativeMain else linuxMain)
      }

      if (jniDir == null) {
        cinterops.create("jni") {
          packageName("jni")
          val jdkIncludes = rootProject.file("jdk").resolve("include")

          includeDirs(mutableListOf<File>().apply {
            add(jdkIncludes)
            add(jdkIncludes.resolve("linux"))
            add(jdkIncludes.resolve("win32"))
            add(jdkIncludes.resolve("darwin"))
          })
         // extraOpts("-verbose")
        }
      }

      cinterops.create("libkipfs") {

        //defFile(project.file("src/nativeInterop/cinterop/KIpfsGo.def"))
        tasks.getAt(interopProcessingTaskName).also {
          it.inputs.files(kipfsBuild.get().outputs)
          it.dependsOn(kipfsBuild.name)
        }

        includeDirs(
          project.buildDir.resolve("native/$platform"),
          rootProject.file("openssl/libs/$platform/include"),
          rootProject.file("go/libs")
        )

        extraOpts(
          mutableListOf<String>().apply {
            //add("-verbose")
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


  //ide mode is linuxX64 target only. Turned off by ./gradlew -Pkipfs.ideMode=false  see ./scripts/publishAll.sh

  jvm {
    compilations["main"].compileKotlinTaskProvider.dependsOn("linkDebugSharedLinuxAmd64")
  }

  linuxX64("linuxAmd64")
  linuxArm32Hfp("linuxArm")
  linuxArm64("linuxArm64")

  androidNativeX86("android386")
  androidNativeX64("androidAmd64")
  androidNativeArm64("androidArm64")
  androidNativeArm32("androidArm")
  mingwX64("windowsAmd64")

  targets.withType(KotlinNativeTarget::class).all {
    configureSharedLib()
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

    val linuxAmd64Test by getting {
      dependsOn(nativeTest)
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

  /*sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
  }*/

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

tasks.withType(KotlinNativeTest::class) {
  kipfsEnvironment(project).forEach {
    environment(it.key, it.value)
  }
}


publishing {

  publications {

    kotlin.targets.withType(KotlinNativeTarget::class) {


/*      compilations["test"].apply {
        println("NATIVE TARGET TEST COMPILATIOn: $this  type: ${this::javaClass}")
      }*/

      binaries.matching {
        it is SharedLibrary && !it.linkTask.target.startsWith("android") &&
            it.buildType == NativeBuildType.RELEASE
      }.all {

        val publicationName = "jni${target.name.capitalize()}"

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

tasks.withType(KotlinNativeHostTest::class).all {
  //println("NativeHostTest: ${this.name} ${this.targetName}")
  environment("LD_LIBRARY_PATH", project.buildDir.resolve("native/$targetName").absolutePath)
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