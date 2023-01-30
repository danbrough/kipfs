plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  //id("com.android.library")
  `maven-publish`
  id("org.jetbrains.dokka")
}



kotlin {


  declareTargets()
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
        api("org.danbrough:klog:_")
      }
    }


    val jvmMain by getting {
      dependencies {
        //dependsOn(baseMain)
        //implementation(KotlinX.coroutines.jdk8)
      }
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

