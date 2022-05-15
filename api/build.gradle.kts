import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
  linuxX64("linuxAmd64")
  jvm()

  if (!ProjectVersions.IDE_MODE) {
    androidNativeX86("android386")
    androidNativeX64("androidAmd64")
    androidNativeArm64("androidArm64")
    androidNativeArm32("androidArm")
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

    val commonMain by getting {
      dependencies {
        implementation(AndroidUtils.logging)
        implementation(KotlinX.serialization.json)
        implementation(KotlinX.serialization.cbor)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }

  afterEvaluate {
    targets.withType(KotlinNativeTarget::class).configureEach {
      compilations["main"].apply {
        dependencies {
          implementation("com.github.danbrough.kipfs:golib:_")
        }
        //this.defaultSourceSet.kotlin.srcDir("src/main/nativeMain")
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

