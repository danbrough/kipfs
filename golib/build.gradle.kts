import org.danbrough.kotlinxtras.binaries.registerLibraryExtension
import org.danbrough.kotlinxtras.binaries.sourceDir

plugins {
  kotlin("multiplatform")
  id("org.danbrough.kotlinxtras.binaries")
  id("org.danbrough.kipfs.go")
}



registerLibraryExtension("golib") {

  version = "0.0.1-beta01"

  sourceDir(project.file("src/go"))

  build {target->
    commandLine("ls")
    doLast {
      buildDir(target).resolve("message.txt").writeText("Build output")
    }
  }
}


kotlin {
  linuxX64()

  val commonMain by sourceSets.getting {
    dependencies {
      implementation("org.danbrough:klog:_")
    }
  }
}