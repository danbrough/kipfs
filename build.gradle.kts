import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform") apply false
  kotlin("plugin.serialization") apply false
  id("com.android.library") apply false
  //id("org.jetbrains.kotlin.jvm") apply false
  id("com.android.application") apply false
  id("org.jetbrains.kotlin.android") apply false

}

buildscript {
  repositories {
    mavenCentral()
  }
}


apply<Project_gradle.ProjectPlugin>()
/*
configure<Project_gradle.ProjectInitExtension> {
  println("2222222222222222222222222222configuring project init extension")
  message.set("Hi")
}
*/


allprojects {


  repositories {
    maven(ProjectProperties.MAVEN_REPO)
    maven("https://h1.danbrough.org/maven")
    mavenCentral()
    google()
  }

  tasks.withType<AbstractTestTask>() {
    testLogging {
      events = setOf(
        TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED
      )
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = true
      showStackTraces = true
    }
    outputs.upToDateWhen {
      false
    }
  }

  tasks.withType(KotlinCompile::class) {
    kotlinOptions {
      jvmTarget = ProjectProperties.KOTLIN_JVM_VERSION
    }
  }


}




