pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}



plugins {
  id("de.fayard.refreshVersions") version "0.40.1"
}


rootProject.name = "KIPFS"

include(":api")
include(":golib")

