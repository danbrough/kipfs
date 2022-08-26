import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
}

repositories {
  maven(Dependencies.SONA_SNAPSHOTS)
  //or for release version
  mavenCentral()
}

kotlin {
  linuxX64()
  macosX64()
  jvm()
  
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.danbrough.kipfs:api:0.0.1-SNAPSHOT")
      }
    }
  }
  
  val commonTest by sourceSets.getting {
    dependencies {
      implementation(kotlin("test"))
    }
  }
  
  val nativeTest by sourceSets.creating {
    dependsOn(commonTest)
  }
  
  targets.withType(KotlinNativeTarget::class).all {
    compilations["test"].apply {
      defaultSourceSet.dependsOn(nativeTest)
    }
  }
}