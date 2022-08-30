import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform") version Dependencies.kotlin
}

group = "org.danbrough.kipfsdemo"
version = "1.0-SNAPSHOT"

val kipfsVersion = "0.0.1-SNAPSHOT"


repositories {
  mavenCentral()
  maven(if (kipfsVersion.contains("-SNAPSHOT")) Dependencies.SONA_SNAPSHOTS else Dependencies.SONA_STAGING)
}

val osName = System.getProperty("os.name")
println("OSNAME: $osName")
kotlin {

  linuxX64()


  //linuxArm64()

  if (osName == "Darwin")
    macosX64()

  androidNativeX86()


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
