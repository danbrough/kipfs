import org.danbrough.kipfs.enableGo
import org.danbrough.kotlinxtras.SHARED_LIBRARY_PATH_NAME
import org.danbrough.kotlinxtras.binaries.LibraryExtension
import org.danbrough.kotlinxtras.capitalize
import org.danbrough.kotlinxtras.core.enableOpenssl3
import org.danbrough.kotlinxtras.platformName
import org.danbrough.kotlinxtras.sharedLibraryPath
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  kotlin("multiplatform")
  id("org.danbrough.kipfs.go") version KIPFS_VERSION
  id("org.danbrough.kotlinxtras.sonatype")
//  id("org.danbrough.kotlinxtras.core")

  `maven-publish`
}


val golib = enableGo {
  deferToPrebuiltPackages = false
}

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
      implementation(kotlin("test"))
      implementation("org.danbrough.kotlinx:kotlinx-coroutines-core:_")
    }
  }

  val nativeMain by sourceSets.creating {
    dependsOn(commonMain)
  }

  val jvmMain by sourceSets.getting {
    dependsOn(commonMain)
  }

  targets.withType<KotlinNativeTarget> {

    binaries {
      sharedLib("kipfs") {
        linkTask.dependsOn(golib.extractArchiveTaskName(konanTarget))
      }
    }

    compilations["main"].apply {


      defaultSourceSet {
        dependsOn(nativeMain)
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









