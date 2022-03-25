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
      //implementation("org.jetbrains.kotlinx:atomicfu:0.17.1-danbroid")
      //implementation("org.jetbrains.kotlins:kotlinx-coroutines-core:1.6.0-native-mt-danbroid")
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-danbroid-native-mt")
      implementation(AndroidUtils.logging)
      //implementation(Ktor.client.curl)
      implementation("io.ktor:ktor-client-curl:1.6.8-danbroid")
      // implementation(kotlin("stdlib"))
      //implementation("danbroid.kipfs:golib:_")
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


  //jvm()

  sourceSets {


    commonTest {
      dependencies {
        implementation(kotlin("test"))

      }
    }

/*    val jvmMain by getting {
      dependencies {
        implementation(Ktor.client.core)
        implementation(Ktor.client.cio)
      }
    }


    val linuxAmd64Main by getting {
      dependencies {
        implementation(Ktor.client.core)
        //   implementation(Ktor.client.cio)
        //  implementation("org.jetbrains.kotlinx:atomicfu:_")
      }

    }

    val linuxAmd64Test by getting {
      dependencies {
        implementation(Ktor.client.curl)
      }
    }*/

/*    val jvmMain by getting {
      dependencies {
        implementation(Ktor.client.cio)
      }
    }

    val jvmTest by getting {

    }*/


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
