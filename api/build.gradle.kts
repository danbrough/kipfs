import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
//  id("com.android.library")
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

kotlin {

//  android()

  linuxX64("linuxAmd64")
  linuxArm32Hfp("linuxArm")
  linuxArm64("linuxArm64")


  val nativeMain by sourceSets.creating {
    dependencies {
    }
  }

  targets.matching { it is KotlinNativeTarget }.all {
    this as KotlinNativeTarget
    binaries {
      executable("kipfsApiTest") {
      }
    }

    sourceSets.getAt("${name}Main").dependsOn(nativeMain)

  }

  sourceSets {


    commonTest {
      dependencies {
        implementation(kotlin("test"))

      }
    }
  }
}

/*

android {
  compileSdk = ProjectVersions.SDK_VERSION
  sourceSets["main"].manifest.srcFile("src/AndroidManifest.xml")

  defaultConfig {
    minSdk = ProjectVersions.MIN_SDK_VERSION
    targetSdk = ProjectVersions.SDK_VERSION
  }
}
*/
