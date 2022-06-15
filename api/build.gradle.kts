import Common_gradle.Common.createTarget
import Common_gradle.GoLib.libsDir

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  //id("com.android.library")
  id("common")

  `maven-publish`
}

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

kotlin {

  BuildEnvironment.nativeTargets.forEach { platform ->
    createTarget(platform)
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

    commonMain {
      dependencies {
        api("com.github.danbrough.klog:klog:_")
        api(KotlinX.serialization.core)
        api(KotlinX.serialization.json)
        api(KotlinX.serialization.cbor)
        api(KotlinX.coroutines.core)

      }
    }

    commonTest {
      dependencies {
        implementation("io.matthewnelson.kotlin-components:encoding-base32:_")
        implementation("io.matthewnelson.kotlin-components:encoding-base64:_")

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

tasks.withType(org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest::class).all {
  environment("LD_LIBRARY_PATH", libsDir(BuildEnvironment.hostPlatform))
}

tasks.withType(org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest::class) {

  val linkTask = rootProject.getTasksByName("linkKipfsDebugSharedLinuxX64",true).first()
  println("GOT TASK $linkTask type: ${linkTask::class}")
  dependsOn(linkTask)

  val libPath =
    "${libsDir(BuildEnvironment.hostPlatform)}${File.pathSeparator}${linkTask.outputs.files.files.first()}"
  println("LIBPATH: $libPath")
  environment(
    "LD_LIBRARY_PATH",
    libPath
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


afterEvaluate {
  publishing {
    repositories {
      maven(ProjectProperties.MAVEN_REPO)
    }
  }
}