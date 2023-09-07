import org.gradle.api.tasks.testing.logging.TestLogEvent
import  org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
//  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.org.jetbrains.dokka) apply false
  alias(libs.plugins.kipfs.go) apply false
}

val kipfsPackage = libs.versions.kipfsPackage.get()
val kipfsVersion = libs.versions.kipfs.get()


allprojects {

  group = kipfsPackage
  version = kipfsVersion


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


