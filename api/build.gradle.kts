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

group = ProjectProperties.projectGroup
version = ProjectProperties.buildVersionName

kotlin {
  
  
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
        api(Dependencies.klog)
        implementation(KotlinX.serialization.core)
        implementation(KotlinX.serialization.json)
        implementation(KotlinX.serialization.cbor)
      }
    }
    
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(project(":golib"))
        implementation("org.danbrough.kotlinx:kotlinx-coroutines-core:_")
      }
    }
    
    val jvmMain by getting {
      dependencies {
        //dependsOn(baseMain)
        //implementation(KotlinX.coroutines.jdk8)
      }
    }
  }
  
  
  targets.withType(KotlinNativeTarget::class) {
/*    compilations["main"].apply {
      defaultSourceSet.dependsOn(sourceSets.getAt("baseMain"))
    }
    compilations["test"].apply {
      defaultSourceSet.dependsOn(sourceSets.getAt("baseTest"))
    }*/
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
  println("GOT TASK $linkTask type: ${linkTask::class}")
  dependsOn(linkTask)
  
  environment(
    if (BuildEnvironment.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH",
    "${BuildEnvironment.hostTarget.goLibsDir(project)}${File.pathSeparator}${linkTask.outputs.files.files.first()}"
  )
  
  
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

