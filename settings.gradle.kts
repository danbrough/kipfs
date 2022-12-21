pluginManagement {
  
  repositories {
    maven(file("build/xtras/maven"))
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

val pluginsOnly: String? by settings

//-PpluginsOnly=true or not specified
if (pluginsOnly == null || pluginsOnly.toBoolean()) {
  include(":plugin")
}

if (!pluginsOnly.toBoolean()) {
  include(":core")
  include(":golib")
  include(":demo")
}

