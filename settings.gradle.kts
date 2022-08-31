pluginManagement {

/*  resolutionStrategy {
    eachPlugin {
      println("PLUGIN ID: ${requested.id.id}")
      if (requested.id.id == "org.jetbrains.kotlin.multiplatform") {
        println("USING THIS ONE!")
        useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
      }
    }
  }*/
  
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}



plugins {
  id("de.fayard.refreshVersions") version "0.40.2"
}


rootProject.name = "kipfs"

include(":openssl")
include(":core")
include(":api")
include(":golib")

