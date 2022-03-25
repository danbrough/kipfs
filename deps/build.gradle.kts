plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME


kotlin {

  android {
  }

  val nativeMain by sourceSets.creating

  fun org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.configure() {
    binaries {
      sharedLib {

      }
    }

    this.compilations["main"].defaultSourceSet.dependsOn(nativeMain)
  }

  androidNativeX86("android386").configure()
  androidNativeX64("androidAmd64").configure()
  androidNativeArm32("androidArm").configure()
  androidNativeArm64("androidArm64").configure()
  mingwX64("windowsAmd64").configure()
  linuxX64("linuxAmd64").configure()
  linuxArm32Hfp("linuxArm").configure()
  linuxArm64("linuxArm64").configure()
}


android {
  compileSdk = ProjectVersions.SDK_VERSION
  sourceSets["main"].manifest.srcFile("src/AndroidManifest.xml")

  defaultConfig {
    minSdk = ProjectVersions.MIN_SDK_VERSION
    targetSdk = ProjectVersions.SDK_VERSION
  }
}
