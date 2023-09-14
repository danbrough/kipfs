import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  //alias(libs.plugins.kotlin.multiplatform)
  id("org.jetbrains.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.org.jetbrains.dokka)

  `maven-publish`
}



kotlin {

  linuxX64()
  linuxArm64()

  mingwX64()
  jvm()

  sourceSets {

    all {
      listOf(
        "kotlin.RequiresOptIn",
        //  "kotlinx.serialization.ExperimentalSerializationApi",
        "kotlin.ExperimentalMultiplatform",
        // "kotlinx.coroutines.ExperimentalCoroutinesApi",
        // "kotlin.time.ExperimentalTime",
      ).forEach {
        languageSettings.optIn(it)
      }
    }

    val commonMain by getting {
      dependencies {
        api(libs.org.danbrough.klog)
      }
    }


    val jvmMain by getting {
      dependencies {
        //dependsOn(baseMain)
        //implementation(KotlinX.coroutines.jdk8)
      }
    }

    val nativeMain by creating

    targets.withType<KotlinNativeTarget>{
      compilations["main"].defaultSourceSet.dependsOn(nativeMain)
    }
  }

}


/*android {
  compileSdk = ProjectProperties.SDK_VERSION
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  namespace = "danbroid.kipfs.api"

  defaultConfig {
    minSdk = ProjectProperties.MIN_SDK_VERSION
    targetSdk = ProjectProperties.SDK_VERSION
  }

  compileOptions {
    sourceCompatibility = ProjectProperties.JAVA_VERSION
    targetCompatibility = ProjectProperties.JAVA_VERSION
  }

}*/

