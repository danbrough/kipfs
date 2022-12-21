import org.gradle.api.tasks.testing.logging.TestLogEvent
import  org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  kotlin("multiplatform") apply false
  id("org.danbrough.kotlinxtras.sonatype") apply false
  id("org.danbrough.kotlinxtras.core") apply false
  id("org.jetbrains.dokka") apply false
  id("org.danbrough.kipfs.go") version KIPFS_VERSION apply false
}

println("Using Kotlin compiler version: ${org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION}")


allprojects {
  group = KIPFS_GROUP
  version = KIPFS_VERSION

  repositories {
    maven("/usr/local/kotlinxtras/build/xtras/maven")
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    mavenCentral()
    google()
  }
  tasks.withType<AbstractTestTask> {
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

}