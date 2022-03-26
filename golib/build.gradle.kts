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

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

val ndkHome =
  System.getenv()["ANDROID_NDK_ROOT"] ?: throw GradleException("ANDROID_NDK_ROOT is not set")

val androidIncludes =
  File("$ndkHome/toolchains/llvm/prebuilt/linux-x86_64/sysroot/usr/include/")

val jniLibsDir = project.buildDir.resolve("jniLibs")


fun kipfsBuild(platform: String) =
  tasks.register<Exec>("kipfsDebug${platform.capitalize()}") {

    commandLine(rootProject.file("bin/build_kipfs.sh"), platform)

    inputs.files(rootProject.fileTree("go") {
      include("**/*.go")
    })

    outputs.files(
      project.files(
        "build/native/$platform/libgokipfs.${if (platform.startsWith("android")) "so" else "a"}",
        "build/native/$platform/libgokipfs.h"
      )
    )

    doLast {
      logger.info("finished building kipfs for $platform")
    }
  }


kotlin {

  val nativeMain by sourceSets.creating

  val linuxMain by sourceSets.creating {
    dependsOn(nativeMain)
  }

  val androidNativeMain by sourceSets.creating {
    dependsOn(nativeMain)
  }


  fun KotlinNativeTarget.configureLibrary() {

    val platform = name
    val kipfsBuild = kipfsBuild(platform)

    println("KONAN TARGET: ${this.konanTarget.name}")
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
        println("Sharedlib: ${this.buildType} class: ${this.buildType.javaClass}")

        if (jniDir != null && buildType == org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG) {
          val copyTask = tasks.register<Copy>("copyToJniLibs${platform.capitalize()}") {
            from(linkTask.outputs)
            from(kipfsBuild.get().outputs)
            into(jniLibsDir.resolve(jniDir))
          }
          kipfsBuild.get().finalizedBy(copyTask)
        }
      }
    }


    compilations["main"].apply {

      println("NATIVECOMPILATION: ${this.name} ${this.konanTarget.name}")


      defaultSourceSet {
        dependsOn(if (jniDir != null) androidNativeMain else linuxMain)
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
          it.dependsOn(kipfsBuild)
        }

        includeDirs(
          project.buildDir.resolve("native/$platform"),
          rootProject.file("openssl/libs/$platform/include")
        )
/*
          linkerOpts(
            "-L${rootProject.file("openssl/libs/$platform/lib").absolutePath}",
            "-L${project.buildDir.resolve("native/$platform").absolutePath}"
          )*/

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

  linuxX64("linuxAmd64") {
    configureLibrary()
  }

  linuxArm32Hfp("linuxArm") {
    configureLibrary()
  }

  linuxArm64("linuxArm64") {
    configureLibrary()
  }

  androidNativeX86("android386") {
    configureLibrary()
  }

  androidNativeX64("androidAmd64") {
    configureLibrary()
  }

  androidNativeArm64("androidArm64") {
    configureLibrary()
  }

  androidNativeArm32("androidArm") {
    configureLibrary()
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

    commonTest {
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

    val androidAndroidTest by getting {
      dependencies {
        implementation(AndroidX.test.coreKtx)
        implementation(AndroidX.test.rules)
        implementation(AndroidX.test.runner)
        implementation(AndroidX.test.ext.junitKtx)
      }
    }

    /*
    val android386Main by getting {
      dependsOn(androidNativeMain)
    }


    val linuxAmd64Main by getting {
      dependsOn(linuxMain)
    }

    val linuxArmMain by getting {
      dependsOn(linuxMain)
    }

    val android386Main by getting {
      dependsOn(androidNativeMain)
    }

    val androidArm64Main by getting {
      dependsOn(androidNativeMain)
    }*/
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


        val publicationName =
          "${baseName}${
            buildType.toString().toLowerCase().capitalize()
          }${target.name.capitalize()}Jni"

        afterEvaluate {
          tasks.named(
            if (buildType == NativeBuildType.DEBUG)
              "mergeDebugJniLibFolders" else "mergeReleaseJniLibFolders"
          ).dependsOn(linkTaskProvider)
        }

        val stripTask = tasks.create("strip${publicationName.capitalize()}",Exec::class){

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
  kotlin.targets.withType(KotlinNativeTarget::class).asMap.values.flatMap { it.binaries.toList() }
    .filterIsInstance<SharedLibrary>()
    .filter {
      it.buildType == org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG &&
          it.linkTask.target.startsWith("android")
    }
    .forEach {
      //  println("ANDROIDTEST depends on ${it.linkTask.target}")
      tasks.named("connectedDebugAndroidTest").dependsOn(it.linkTaskProvider)
    }
}