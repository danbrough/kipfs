package org.danbrough.kipfs

import org.danbrough.kotlinxtras.binaries.LibraryExtension
import org.danbrough.kotlinxtras.binaries.registerLibraryExtension
import org.danbrough.kotlinxtras.binaries.sourceDir
import org.danbrough.kotlinxtras.enableOpenssl
import org.danbrough.kotlinxtras.sharedLibExtn
import org.gradle.api.Plugin
import org.gradle.api.Project

fun Project.enableGo(extnName:String = "golib",config:LibraryExtension.()->Unit = {}): LibraryExtension{
  val openSSL = enableOpenssl()

  return extensions.findByName(extnName) as? LibraryExtension ?: registerLibraryExtension(extnName) {

    version = "0.0.1-beta01"

    sourceDir(project.rootProject.file("go"))

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

    config()
  }
}

class GoPlugin : Plugin<Project> {
  override fun apply(target: Project) {

  }
}
