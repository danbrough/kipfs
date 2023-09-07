import org.danbrough.kipfs.go.xtrasGoBuilder
import org.danbrough.xtras.wolfssl.xtrasWolfSSL
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.xtras.wolfssl)
  alias(libs.plugins.kipfs.go)
  `maven-publish`
}


val ssl = xtrasWolfSSL()

val golib = xtrasGoBuilder(ssl, projectDir.resolve("src/go"),"kipfs") {

  cinterops {
    interopsPackage = "libkipfs"
    headers = """
          |headers = libkipfsgo.h
          |linkerOpts = -lkipfsgo
          |""".trimMargin()

  }
}


kotlin {
  linuxX64()
  //linuxArm64()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.org.danbrough.klog)
        implementation(libs.kotlinx.coroutines.core)
        implementation(project(":core"))
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val nativeMain by creating {
      dependsOn(commonMain)
    }

    targets.withType<KotlinNativeTarget> {
      compilations["main"].apply {
        defaultSourceSet.dependsOn(nativeMain)

        cinterops {
          create("golib") {
            packageName = "libkipfs"
            defFile = rootDir.resolve("build/xtras/cinterops/xtras_kipfs.def")
          }
          if (konanTarget.family != Family.ANDROID){
            create("jni") {
              packageName = "platform.android"
              defFile = projectDir.resolve("src/interop/jni.def")
              includeDirs.allHeaders(projectDir.resolve("src/include"))
            }
          }
        }
      }

    }


  }
}

/*


val golib = enableGo {
  deferToPrebuiltPackages = false
}

kotlin {

  declareTargets()

  val commonMain by sourceSets.getting {
    dependencies {
      implementation(libs.org.danbrough.klog)
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









*/
