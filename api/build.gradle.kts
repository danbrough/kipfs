import Common_gradle.Common.createTarget

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  //id("com.android.library")
  id("common")

  `maven-publish`
}

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

kotlin {

  BuildEnvironment.nativeTargets.forEach { platform ->
    createTarget(platform)
  }

  jvm()
  //android()

  sourceSets {

    all {
      listOf(
        "kotlin.RequiresOptIn",
        "kotlinx.serialization.ExperimentalSerializationApi",
        "kotlin.ExperimentalMultiplatform",
        // "kotlinx.coroutines.ExperimentalCoroutinesApi",
        // "kotlin.time.ExperimentalTime",
      ).forEach {
        languageSettings.optIn(it)
      }
    }

    commonMain {
      dependencies {
        implementation(AndroidUtils.logging)
        api(KotlinX.serialization.core)
        api(KotlinX.serialization.json)
        api(KotlinX.serialization.cbor)
        api(KotlinX.coroutines.core)
        implementation("io.matthewnelson.kotlin-components:encoding-base32:_")

      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
        implementation(project(":golib"))
      }
    }

/*    val androidMain by getting {
      dependencies {
        // implementation(KotlinX.coroutines.android)
      }
    }*/

    val jvmMain by getting {
      dependencies {
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


afterEvaluate {
  publishing {
    repositories {
      maven(ProjectProperties.MAVEN_REPO)
    }
  }
}