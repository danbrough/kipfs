import com.android.build.api.variant.AndroidTest
import com.android.build.gradle.internal.tasks.AndroidTestTask
import com.android.build.gradle.tasks.factory.AndroidUnitTest
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import  org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest


plugins {
  kotlin("multiplatform") apply false
  kotlin("plugin.serialization") apply false
  id("com.android.library") apply false
  id("org.jetbrains.kotlin.jvm") apply false
  id("com.android.application") apply false
  id("org.jetbrains.kotlin.android") apply false
}

buildscript {
  repositories {
    mavenCentral()
  }
}


apply("project.gradle.kts")

allprojects {

  repositories {
    maven(ProjectVersions.MAVEN_REPO)
    maven("https://h1.danbrough.org/maven")
    mavenCentral()
    google()
  }

  tasks.withType<AbstractTestTask>() {
    testLogging {
      events = setOf(
        TestLogEvent.PASSED,
        TestLogEvent.SKIPPED,
        TestLogEvent.FAILED
      )
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = true
      showStackTraces = true
    }
    outputs.upToDateWhen {
      false
    }
  }

  val hostPlatform = ProjectVersions.TARGET_HOST
  val golibBuildDir = rootDir.resolve("golib/build")
  val libPath =
    golibBuildDir.resolve("bin/$hostPlatform/debugShared").absolutePath + File.pathSeparator +
        golibBuildDir.resolve("native/$hostPlatform").absolutePath

  tasks.withType<Test>().all {
    dependsOn(":golib:linkDebugShared${hostPlatform.capitalize()}")
    environment("LD_LIBRARY_PATH", libPath)
    ProjectVersions.properties.forEach {
      environment(it.key, it.value.toString())
    }
  }


  tasks.withType<KotlinJvmCompile>().all {
    kotlinOptions {
      jvmTarget = ProjectVersions.KOTLIN_JVM_VERSION
    }
  }

}




