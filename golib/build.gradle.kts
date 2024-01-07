import org.danbrough.kipfs.go.xtrasGoBuilder
import org.danbrough.xtras.declareHostTarget
import org.danbrough.xtras.openssl.xtrasOpenSSL
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.xtras.openssl)
  alias(libs.plugins.kipfs.go)
  `maven-publish`
}


val ssl = xtrasOpenSSL {
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
//  linuxArm64()


  val commonMain by sourceSets.getting {
    dependencies {
      implementation(libs.org.danbrough.klog)
      implementation(libs.kotlinx.coroutines.core)
      implementation(project(":core"))
    }
  }

  sourceSets.commonTest {
    dependencies {
      implementation(kotlin("test"))
    }
  }

  val nativeMain by sourceSets.creating {
    dependsOn(commonMain)
  }


  targets.withType<KotlinNativeTarget> {
    compilations["main"].apply {
      defaultSourceSet.dependsOn(nativeMain)

      cinterops {
        if (konanTarget.family != Family.ANDROID) {
          cinterops.create("jniHeaders") {
            packageName("platform.android")
            defFile = project.file("src/interop/jni.def")
            if (konanTarget.family.isAppleFamily) {
              includeDirs(project.file("src/include"))
              includeDirs(project.file("src/include/darwin"))
            } else if (konanTarget.family == Family.MINGW) {
              includeDirs(project.file("src/include"))
              includeDirs(project.file("src/include/win32"))
            } else if (konanTarget.family == Family.LINUX) {
              includeDirs(project.file("src/include"))
              includeDirs(project.file("src/include/linux"))
            }
          }
        }
      }
    }
  }
}
