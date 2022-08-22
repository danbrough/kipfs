import BuildEnvironment.hostIsMac
import BuildEnvironment.hostTarget
import BuildEnvironment.platformNameCapitalized
import BuildEnvironment.registerTarget
import GoLib.goLibsDir
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  //id("com.android.library")
  `maven-publish`
  id("org.jetbrains.dokka")
  signing
  
}

group = ProjectProperties.projectGroup
version = ProjectProperties.buildVersionName

kotlin {
  
  
  BuildEnvironment.nativeTargets.forEach {target->
    registerTarget(target)
  }
  
  jvm()
  //android()
  
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
        api(Dependencies.klog)
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

