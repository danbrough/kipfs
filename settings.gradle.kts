pluginManagement {

  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "kotlin-multiplatform") {
        useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
      }
    }
  }

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
include(":golib")

/*include(":api")
include(":golib")*/
//include(":demos:ktor")
