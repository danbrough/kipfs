pluginManagement {
  
  repositories {
  //  maven("https://s01.oss.sonatype.org/content/groups/staging/")
   maven("/usr/local/kotlinxtras/build/m2")
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}



plugins {
  id("de.fayard.refreshVersions") version "0.50.2"
}


rootProject.name = "kipfs"

include(":core")
include(":api")
include(":golib")

