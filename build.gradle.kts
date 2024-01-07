import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.org.jetbrains.dokka) apply false
  alias(libs.plugins.kipfs.go) apply false
}

val kipfsPackage = libs.versions.kipfsPackage.get().toString()
val kipfsVersion = libs.versions.kipfs.get().toString()

val xtrasMavenDir = if (hasProperty("xtras.dir.maven")) File(property("xtras.dir.maven").toString())
else if (hasProperty("xtras.dir")) File(property("xtras.dir").toString()).resolve("maven")
else error("Neither xtras.dir.maven or xtras.dir are set")

allprojects {


  group = kipfsPackage
  version = kipfsVersion

  repositories {
    maven(xtrasMavenDir) {
      name = "xtras"
    }
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    mavenCentral()
    google()
  }

  tasks.withType<AbstractTestTask> {
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

}

File("/tmp/gradle_settings.log").appendText(gradle.startParameter.taskNames.joinToString("\n") {
  "BUILD TASK: $it"
})

