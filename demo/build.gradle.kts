import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform") version Dependencies.kotlin
}

group = "org.danbrough.kipfsdemo"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven(Dependencies.SONA_STAGING)
}

kotlin {

  linuxX64()
  //linuxArm64()
  macosX64()

  val kipfsVersion = "0.0.1-alpha06"

  val nativeMain by sourceSets.creating {
    dependencies {
      implementation("org.danbrough.kipfs:openssl:$kipfsVersion")
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
