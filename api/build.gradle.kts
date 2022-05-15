import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
//  id("com.android.library")
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME

kotlin {

//  android()


  linuxX64("linuxAmd64")

  if (!ProjectVersions.IDE_MODE) {
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

  val nativeMain by sourceSets.creating {
    dependencies {
      //implementation(KotlinX.serialization.json)
      implementation("com.github.danbrough.kipfs:golib:_")
    }
  }

  afterEvaluate {
    targets.withType(KotlinNativeTarget::class).configureEach {
      compilations["main"].apply {
        defaultSourceSet.dependsOn(nativeMain)
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
