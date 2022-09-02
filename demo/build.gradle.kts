import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform") version Dependencies.kotlin
 // id("com.android.library")
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

  if (osName == "Mac OS X")
    macosX64()

  androidNativeX86(){
    println("FRAMEWORK $this")
    println("RELEASE ${this.RELEASE}")
    println("apiElementsConfigurationName ${this.apiElementsConfigurationName}")

  }
  androidNativeX64(){
    println("FRAMEWORK $this")
    println("RELEASE ${this.RELEASE}")
    println("apiElementsConfigurationName ${this.apiElementsConfigurationName}")

  }

  val nativeMain by sourceSets.creating {
    dependencies {
     // implementation("org.danbrough.kipfs:openssl:_")
    }
  }

  val commonMain by sourceSets.getting {
    dependencies {
      implementation("org.danbrough:klog:_")
    }
  }

  targets.withType(KotlinNativeTarget::class).all {
    compilations["main"].apply {
      defaultSourceSet.dependsOn(nativeMain)
      
      cinterops{

        cinterops.create("openssl") {

          packageName("libopenssl")
          defFile = project.file("src/openssl.def")
          extraOpts("-verbose")


//          includeDirs( konanTarget.opensslPrefix(project).resolve("include"))
//          extraOpts(listOf("-libraryPath", konanTarget.opensslPrefix(project).resolve("lib")))
        }


      }
    }

    binaries {
      executable("openSSLDemo") {
        entryPoint = "kipfs.demo.openssl.main"
      }
    }
  }
}

