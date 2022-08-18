import BuildEnvironment.hostIsMac
import BuildEnvironment.hostTarget
import BuildEnvironment.platformNameCapitalized
import BuildEnvironment.registerTarget
import GoLib.goLibsDir
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  //id("com.android.library")
  `maven-publish`
  id("org.jetbrains.dokka")
  signing
}

group = ProjectProperties.projectGroup
version = ProjectProperties.buildVersionName

kotlin {
  
  //kotlinx-serialization currently doesn't support android native
  BuildEnvironment.nativeTargets.filter { it.family != Family.ANDROID }
    .forEach { target ->
      registerTarget(target)
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
    
    val commonMain by getting {
      dependencies {
        implementation(project(":api"))
        implementation(KotlinX.serialization.core)
        implementation(KotlinX.serialization.cbor)
        implementation(KotlinX.serialization.json)
  
      }
    }
    
  }
  
}
