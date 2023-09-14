import org.gradle.api.tasks.testing.logging.TestLogEvent
import  org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.org.jetbrains.dokka) apply false
  alias(libs.plugins.kipfs.go) apply false
}

val kipfsPackage = libs.versions.kipfsPackage.get().toString()
val kipfsVersion = libs.versions.kipfs.get().toString()


allprojects {

  group = kipfsPackage
  version = kipfsVersion

  repositories {
    maven("/usr/local/xtras/maven") {
      name = "xtras"
    }
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

