import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import  org.gradle.api.tasks.testing.logging.TestExceptionFormat


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


  tasks.withType<KotlinJvmCompile>().all {
    kotlinOptions {
      jvmTarget = ProjectVersions.KOTLIN_JVM_VERSION
    }
  }

}

