import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
    commonMain {
      dependencies {
        implementation(AndroidUtils.logging)
        implementation(KotlinX.serialization.json)
        implementation(KotlinX.serialization.cbor)
        implementation(project(":golib"))
      }
    }

    commonTest {
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


  targets.all {
    compilations.all {
      kotlinOptions {
        listOf(
          "kotlin.RequiresOptIn",
          //  "kotlinx.serialization.InternalSerializationApi",
          "kotlinx.serialization.ExperimentalSerializationApi",
          // "kotlinx.coroutines.ExperimentalCoroutinesApi",
          // "kotlin.time.ExperimentalTime",
        ).map { "-Xopt-in=$it" }.also {

          freeCompilerArgs = freeCompilerArgs + it
        }
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
