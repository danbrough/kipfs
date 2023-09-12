import org.danbrough.kipfs.go.xtrasGoBuilder
import org.danbrough.xtras.declareHostTarget
import org.danbrough.xtras.wolfssl.xtrasWolfSSL
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.xtras.wolfssl)
  alias(libs.plugins.kipfs.go)
  `maven-publish`
}


val ssl = xtrasWolfSSL {
  resolveBinariesFromMaven = true
}

val golib =
  xtrasGoBuilder(
    ssl,
    goDir = projectDir.resolve("src/go"), name = "kipfs", modules = "./libs"
  ) {
    cinterops {
      interopsPackage = "libkipfs"
      headers = """
          |headers = libkipfsgo.h defs.h
          |linkerOpts = -lkipfsgo
          |""".trimMargin()

    }
  }


kotlin {
  declareHostTarget()
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


  }
}
