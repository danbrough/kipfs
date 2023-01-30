import org.danbrough.kotlinxtras.binaries.registerLibraryExtension
import org.danbrough.kotlinxtras.binaries.sourceDir
import org.danbrough.kotlinxtras.enableOpenssl
import org.danbrough.kotlinxtras.sharedLibExtn
import org.jetbrains.kotlin.builtins.konan.KonanBuiltIns
import org.jetbrains.kotlin.commonizer.KonanDistribution
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractExecutable
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.util.Date
import org.danbrough.kipfs.enableGo

plugins {
  kotlin("multiplatform")
  id("org.danbrough.kipfs.go") version KIPFS_VERSION
  id("org.danbrough.kotlinxtras.sonatype")


  `maven-publish`
}


val golib = enableGo {

}

val openSSL = enableOpenssl()

kotlin {

  declareTargets()

  val commonMain by sourceSets.getting {
    dependencies {
      implementation("org.danbrough:klog:_")
      implementation(project(":core"))
    }
  }

  val commonTest by sourceSets.getting {
    dependencies {
      implementation("org.danbrough.kotlinx:kotlinx-coroutines-core:_")
    }
  }

  val nativeMain by sourceSets.creating {
    dependsOn(commonMain)
  }

  val jniMain by sourceSets.creating {
    dependsOn(nativeMain)
  }

  targets.withType<KotlinNativeTarget> {

/*    binaries.all {
      if (this is Executable) {
        runTask?.apply {
          val ldLibKey = if (HostManager.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH"
          val libPath = environment[ldLibKey]
          val newLibPath =
            (libPath?.let { "$it${File.pathSeparator}" }
              ?: "") + "${golib.libsDir(konanTarget)}/lib"
          //println("----------------------ADDING: $ldLibKey:$newLibPath")
          environment(ldLibKey, newLibPath)
        } ?: println("no run task")
      }
    }*/

    compilations["main"].apply {

      defaultSourceSet {
        dependsOn(jniMain)
      }

      if (konanTarget.family != org.jetbrains.kotlin.konan.target.Family.ANDROID) {
        cinterops.create("jni_headers") {
          packageName("platform.android")
          defFile = project.file("src/interop/jni.def")
          if (konanTarget.family.isAppleFamily) {
            includeDirs(project.file("src/include"))
            includeDirs(project.file("src/include/darwin"))
          } else if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.MINGW) {
            includeDirs(project.file("src/include"))
            includeDirs(project.file("src/include/win32"))
          } else if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.LINUX) {
            includeDirs(project.file("src/include"))
            includeDirs(project.file("src/include/linux"))
          }
        }
      }
    }
  }
}


tasks.create("stuff") {
  doFirst {

    project.tasks.withType(org.gradle.api.publish.maven.tasks.PublishToMavenRepository::class.java).all{

      println("publishing task: $this")
    }

  }
}









