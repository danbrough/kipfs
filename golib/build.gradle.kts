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

plugins {
  kotlin("multiplatform")
  id("org.danbrough.kotlinxtras.core")
  id("org.danbrough.kotlinxtras.sonatype")
}


group = KIPFS_GROUP
val openSSL = enableOpenssl()

val golib = registerLibraryExtension("golib") {


  version = "0.0.1-beta01"

  sourceDir(project.file("src/go"))

  cinterops {
    headers = """
          |headers = libkipfsgo.h
          |linkerOpts = -lkipfsgo 
          |""".trimMargin()
  }

  build { target ->


    dependsOn(openSSL.resolveArchiveTaskName(target))

    inputs.files(project.fileTree(workingDir) {
      include("**/*.go")
      include("**/*.c")
      include("**/*.h")
      include("**/*.mod")
    })

    openSSL.addBuildFlags(target, environment)
    environment["CGO_CFLAGS"] = environment["CFLAGS"]
    environment["CGO_LDFLAGS"] = environment["LDFLAGS"]


    val buildDir = buildDir(target)
    val libFile = buildDir.resolve("lib/libkipfsgo.${target.sharedLibExtn}")
    val modules = "github.com/danbrough/kipfs/libs"

    doLast {
      val headersDir = buildDir.resolve("include")
      if (!headersDir.exists()) headersDir.mkdirs()
      workingDir.resolve("libs/defs.h").copyTo(headersDir.resolve("defs.h"), true)
      buildDir.resolve("lib/libkipfsgo.h").also {
        it.copyTo(headersDir.resolve("libkipfsgo.h"), true)
        it.delete()
      }
    }

    commandLine(
      binaries.goBinary,
      "build",
      "-v",
      "-trimpath",
      "-buildmode=c-shared",
      "-tags=shell,openssl",
      "-o",
      libFile,
      modules
    )
  }
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


tasks.withType(KotlinNativeTest::class).all {
  val ldLibKey = if (HostManager.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH"
  val konanTarget = if (HostManager.hostIsMac) KonanTarget.MACOS_X64 else KonanTarget.LINUX_X64
  val libPath = environment[ldLibKey]
  val newLibPath =
    (libPath?.let { "$it${File.pathSeparator}" } ?: "") + "${golib.libsDir(konanTarget)}/lib" + File.pathSeparatorChar + "${openSSL.libsDir(konanTarget)}/lib"
  environment(ldLibKey, newLibPath)
}


