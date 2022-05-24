import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  id("com.android.library")
  `maven-publish`
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

val jniLibsDir = project.buildDir.resolve("jniLibs")



android {
  compileSdk = ProjectVersions.SDK_VERSION
  namespace = ProjectVersions.GROUP_ID
  ndkVersion = ProjectVersions.NDK_VERSION

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




fun kipfsBuild(platform: String) = tasks.register<Exec>("kipfsDebug${platform.capitalize()}") {
  environment("ANDROID_NDK_ROOT", android.ndkDirectory.absolutePath)
  environment("PLATFORM", platform)
  doLast {
    logger.warn("kipfs build finished for $platform")
  }

  commandLine(rootProject.file("bin/build_kipfs.sh"))

  inputs.files(rootProject.fileTree("go") {
    include("**/*.go")
    include("**/*.c")
    include("**/*.h")
  } + rootProject.file("bin/build_kipfs.sh"))

  outputs.files(
    project.buildDir.resolve("native/$platform/libgokipfs.so"),
    project.buildDir.resolve("native/$platform/libgokipfs.h"),
  )

  doLast {
    logger.info("finished building kipfs for $platform")
  }

}



kotlin {

  fun KotlinNativeTarget.configureSharedLib() {
    val platform = name
    val kipfsBuild = kipfsBuild(platform)

    val androidJniLibDir = when (platform) {
      "android386" -> "x86"
      "androidAmd64" -> "x86_64"
      "androidArm" -> "armeabi-v7a"
      "androidArm64" -> "arm64-v8a"
      else -> null
    }

    binaries {
      sharedLib {
        baseName = "kipfs"

        if (androidJniLibDir != null && buildType == NativeBuildType.DEBUG) {
          //copy android native library and header to androidJniLibDir directory
          tasks.register<Copy>("copyToJniLibs${platform.capitalize()}") {
            from(linkTask.outputs)
            from(kipfsBuild.get().outputs)
            into(jniLibsDir.resolve(androidJniLibDir))
          }.also {
            linkTask.finalizedBy(it)
          }
        }
      }
    }

    /*compilations["test"].apply {
      println("CONFIGURING COMPILATION $name")
    }
*/
    compilations["test"].apply {
      defaultSourceSet {
        kotlin.srcDir("src/nativeTest/kotlin")
      }
    }

    compilations["main"].apply {

      logger.info("NATIVE COMPILATION: $name ${konanTarget.name}")

      defaultSourceSet {
        kotlin.srcDir("src/nativeMain/kotlin")
      }

      //android targets already have jni cinterop.
      if (androidJniLibDir == null)
        cinterops.create("jni") {
          //use same package name as the android code
          packageName("platform.android")
          //   extraOpts("-verbose")
        }


      cinterops.create("libkipfs") {

        //defFile(project.file("src/nativeInterop/cinterop/KIpfsGo.def"))
        tasks.getAt(interopProcessingTaskName).apply {
          inputs.files(kipfsBuild.get().outputs)
          dependsOn(kipfsBuild.name)
        }

        includeDirs(mutableListOf<File>().apply {
          add(rootProject.file("openssl/libs/$platform/include"))
          add(rootProject.file("go/libs"))
        })

        includeDirs(
          project.buildDir.resolve("native/$platform"),
          rootProject.file("openssl/libs/$platform/include"),
          rootProject.file("go/libs")
        )

        extraOpts(mutableListOf<String>().apply {
          //add("-verbose")
          //-Lopenssl/libs/linuxArm64/lib -L./golib/build/native/linuxArm64
          add("-libraryPath")
          add("${rootProject.file("openssl/libs/$platform/lib")}")
          add("-libraryPath")
          add("${project.buildDir.resolve("native/$platform")}")
        })
      }
    }
  }


  android()
  linuxX64(ProjectVersions.PLATFORM_LINUX_AMD64)
  jvm()



  if (!ProjectVersions.IDE_MODE) {
/*    androidNativeX86("android386")
    androidNativeX64("androidAmd64")
    androidNativeArm64("androidArm64")
    androidNativeArm32("androidArm")*/
    mingwX64("windowsAmd64")
    linuxArm32Hfp("linuxArm")
    linuxArm64("linuxArm64")
  }

  targets.withType(KotlinNativeTarget::class).all {
    configureSharedLib()
  }

  sourceSets {

    all {
      languageSettings.optIn("kotlin.RequiresOptIn")
    }


    val commonMain by getting {
      dependencies {
        implementation(AndroidUtils.logging)
        implementation(project(":api"))
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
        implementation(KotlinX.coroutines.core)
      }
    }

    val jni by creating {
      dependsOn(commonMain)
    }

    val androidMain by getting {
      dependsOn(jni)
    }

    val androidTest by getting {
      dependsOn(androidMain)
    }

    val jvmMain by getting {
      dependsOn(jni)
    }

    val androidAndroidTest by getting {
      dependsOn(androidMain)
      dependencies {
        implementation(AndroidX.test.coreKtx)
        implementation(AndroidX.test.rules)
        implementation(AndroidX.test.runner)
        implementation(AndroidX.test.ext.junitKtx)
      }
    }


  }


}


afterEvaluate {
  //make connectedDebugAndroidTest tasks dependent on the jni shared library
  kotlin.targets.withType(KotlinNativeTarget::class).asMap.values.flatMap { it.binaries.toList() }
    .filterIsInstance<SharedLibrary>().filter {
      it.buildType == NativeBuildType.DEBUG && it.linkTask.target.startsWith("android")
    }.forEach {
      tasks.named("connectedDebugAndroidTest").dependsOn(it.linkTaskProvider)
    }

  publishing {
    publications {

      kotlin.targets.withType(KotlinNativeTarget::class) {


/*      compilations["test"].apply {
        println("NATIVE TARGET TEST compilation: $this  type: ${this::javaClass}")
      }*/

        binaries.matching {
          it is SharedLibrary && !it.linkTask.target.startsWith("android") && it.buildType == NativeBuildType.RELEASE
        }.all {

          val publicationName = "jni${target.name.capitalize()}"

          afterEvaluate {
            tasks.named(if (buildType == NativeBuildType.DEBUG) "mergeDebugJniLibFolders" else "mergeReleaseJniLibFolders")
              .dependsOn(linkTaskProvider)
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


}

/*
PRESET: android
PRESET: androidNativeArm32
PRESET: androidNativeArm64
PRESET: androidNativeX64
PRESET: androidNativeX86
PRESET: iosArm32
PRESET: iosArm64
PRESET: iosSimulatorArm64
PRESET: iosX64
PRESET: js
PRESET: jsBoth
PRESET: jsIr
PRESET: jvm
PRESET: jvmWithJava
PRESET: linuxArm32Hfp
PRESET: linuxArm64
PRESET: linuxMips32
PRESET: linuxMipsel32
PRESET: linuxX64
PRESET: macosArm64
PRESET: macosX64
PRESET: mingwX64
PRESET: mingwX86
PRESET: tvosArm64
PRESET: tvosSimulatorArm64
PRESET: tvosX64
PRESET: wasm
PRESET: wasm32
PRESET: watchosArm32
PRESET: watchosArm64
PRESET: watchosSimulatorArm64
PRESET: watchosX64
PRESET: watchosX86


 */
