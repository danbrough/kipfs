import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import java.io.File

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
//  id("com.android.library")
  `maven-publish`
}

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

kotlin {
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