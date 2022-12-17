pluginManagement {
  
  repositories {
    maven("/usr/local/kotlinxtras/build/xtras/maven")
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}



plugins {
  id("de.fayard.refreshVersions") version "0.51.0"
}
refreshVersions { // Optional: configure the plugin

}

rootProject.name = "kipfs"

include(":golib")

