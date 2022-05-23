import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import java.io.File

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  // id("com.android.library")
  `maven-publish`
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME


kotlin {


  // android()
  linuxX64(ProjectVersions.PLATFORM_LINUX_AMD64)
  linuxArm64(ProjectVersions.PLATFORM_LINUX_ARM64)
 // linuxArm32Hfp(ProjectVersions.PLATFORM_LINUX_ARM32)

  jvm()


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
        implementation(KotlinX.coroutines.core)
        implementation(Ktor.client.core)
        implementation(Ktor.network.network)

      }
    }

   val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        //implementation(project(":golib"))
      }
    }

    val nativeMain by creating {
      dependsOn(commonMain)
      dependencies {
//        implementation(Ktor.client.curl)
        implementation(Ktor.client.cio)

      }
    }
    val nativeTest by creating{
      dependsOn(nativeMain)
    }

    val linuxAmd64Main by getting {
      dependsOn(nativeMain)
      dependencies {
        //implementation(Ktor.client.curl)
        implementation(Ktor.client.cio)


      }
      //kotlin.srcDir("src/nativeMain/kotlin")
    }

    val jvmMain by getting {
      dependsOn(commonMain)
      dependencies {
        implementation(Ktor.client.cio)
      }
    }
    val linuxArm64Main by getting {
      dependsOn(nativeMain)

      //kotlin.srcDir("src/nativeMain/kotlin")
    }

    val linuxAmd64Test by getting {
      dependsOn(nativeTest)
      //kotlin.srcDir("src/nativeTest/kotlin")
    }

/*    val linuxArmMain by getting {
      dependsOn(nativeMain)
    }*/

  }
}

kotlin.targets.withType<KotlinNativeTarget>().all {
  binaries.executable("ktorDemo") {

    entryPoint("danbroid.demo.main")
  }

  compilations["main"].apply {

  }
}

/*

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

*/
