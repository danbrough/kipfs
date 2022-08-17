import BuildEnvironment.platformName
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  kotlin("multiplatform") apply false
  //kotlin("plugin.serialization") apply false
  //id("com.android.library") apply false
  //id("org.jetbrains.kotlin.jvm") apply false
  //id("com.android.application") apply false
//  id("org.jetbrains.kotlin.android")
  `maven-publish`
  id("org.jetbrains.dokka")
}

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

ProjectProperties.init(project)

allprojects {
  repositories {
    mavenCentral()
    google()
    // maven("https://s01.oss.sonatype.org/content/repositories/releases/")
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
/*
subprojects {
  plugins.apply("org.jetbrains.dokka")
}
*/


tasks.register<Delete>("deleteDocs") {
  setDelete(file("docs/api"))
}

tasks.register<Copy>("copyDocs") {
  dependsOn("deleteDocs")
  from(buildDir.resolve("dokka"))
  destinationDir = file("docs/api")
}


tasks.dokkaHtmlMultiModule.configure {
  outputDirectory.set(buildDir.resolve("dokka"))
  finalizedBy("copyDocs")
}

val javadocJar by tasks.registering(Jar::class) {
  archiveClassifier.set("javadoc")
  from(tasks.dokkaHtml)
}

