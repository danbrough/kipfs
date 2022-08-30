import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform") version Dependencies.kotlin
}

group = "org.danbrough.kipfsdemo"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven(Dependencies.SONA_SNAPSHOTS)
}

kotlin {

  linuxX64()
  //linuxArm64()
  macosX64()

  val nativeMain by sourceSets.creating {
    dependencies {
      implementation("org.danbrough.kipfs:openssl:0.0.1-SNAPSHOT")
    }
  }

  val commonMain by sourceSets.getting {
    dependencies {
      implementation("org.danbrough:klog:0.0.1-beta06")
    }
  }

  targets.withType(KotlinNativeTarget::class).all {
    compilations["main"].apply {
      defaultSourceSet.dependsOn(nativeMain)
    }

    binaries {
      executable("openSSLDemo") {
        entryPoint = "kipfs.demo.openssl.main"
      }
    }
  }
}
