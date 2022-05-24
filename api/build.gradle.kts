import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import java.io.File

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.android.library")
  `maven-publish`
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

kotlin {

  android()
  linuxX64(ProjectVersions.PLATFORM_LINUX_AMD64)
  jvm()

  if (!ProjectVersions.IDE_MODE) {
    /*
    androidNativeX86("android386")
    androidNativeX64("androidAmd64")
    androidNativeArm64("androidArm64")
    androidNativeArm32("androidArm")
     */

    mingwX64("windowsAmd64")
    linuxArm32Hfp("linuxArm")
    linuxArm64("linuxArm64")
  }

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

    val androidMain by getting {
      dependencies {
        // implementation(KotlinX.coroutines.android)
      }
    }

    val jvmMain by getting {
      dependencies {
        //implementation(KotlinX.coroutines.jdk8)
      }
    }
  }
}


android {
  compileSdk = ProjectVersions.SDK_VERSION
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  namespace = "danbroid.kipfs.api"

  defaultConfig {
    minSdk = ProjectVersions.MIN_SDK_VERSION
    targetSdk = ProjectVersions.SDK_VERSION
  }

  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

}


afterEvaluate {
  publishing {
    repositories {
      maven(ProjectVersions.MAVEN_REPO)
    }
  }
}