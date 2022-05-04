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

rootProject.name = "kipfs"
include(":api")
include(":golib")
//include(":deps")
include(":demos:native_cmdline")
include(":demos:jvm")
//include(":demo")


