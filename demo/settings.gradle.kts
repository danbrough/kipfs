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


rootProject.name = "kipfs_demo"

