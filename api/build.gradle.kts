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
}



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
        implementation(project(":core"))
        implementation(KotlinX.serialization.core)
        implementation(KotlinX.serialization.cbor)
        implementation(KotlinX.serialization.json)
        
      }
    }
    
    
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(project(":golib"))
        implementation(Dependencies.coroutines_core)
      }
    }
    
  }
}

tasks.withType(org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest::class).all {
  environment(
    if (BuildEnvironment.hostTarget.family.isAppleFamily) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH",
    BuildEnvironment.hostTarget.goLibsDir(project)
  )
}

tasks.withType(org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest::class) {
  
  val hostTarget = BuildEnvironment.hostTarget
  
  val linkTask =
    rootProject.getTasksByName("linkKipfsDebugShared${hostTarget.platformNameCapitalized}", true)
      .first()
  dependsOn(linkTask)
  
  environment(
    if (BuildEnvironment.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH",
    "${BuildEnvironment.hostTarget.goLibsDir(project)}${File.pathSeparator}${linkTask.outputs.files.files.first()}"
  )
  
  
}

