package org.danbrough.kipfs.go

import org.danbrough.xtras.XTRAS_TASK_GROUP
import org.danbrough.xtras.XtrasDSLMarker
import org.danbrough.xtras.goArch
import org.danbrough.xtras.goOS
import org.danbrough.xtras.library.XtrasLibrary
import org.danbrough.xtras.library.xtrasCreateLibrary
import org.danbrough.xtras.library.xtrasRegisterSourceTask
import org.danbrough.xtras.log
import org.danbrough.xtras.sharedLibExtn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.internal.tooling.model.TaskNameComparator
import java.io.File


class GoPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.log("GoPlugin::apply()")
  }
}

const val GO_EXTN_NAME = "gobuild"
const val GO_EXTN_VERSION = "0.0.1-beta01"


@XtrasDSLMarker
fun Project.xtrasGoBuilder(
  ssl: XtrasLibrary,
  goDir: File,
  name: String = GO_EXTN_NAME,
  modules:String,
  configure: XtrasLibrary.() -> Unit = {},
) = xtrasCreateLibrary(name, GO_EXTN_VERSION, ssl) {

  configure()

  supportedTargets.forEach { target ->

    val extractSourceTask = extractSourceTaskName(target)
    val sourceDir = sourcesDir(target)

    tasks.register(extractSourceTask) {
      group = XTRAS_TASK_GROUP
      inputs.dir(goDir)
      outputs.dir(sourceDir)
      doFirst {
        project.log("copying $goDir to $sourceDir")
      }
      actions.add {
        goDir.copyRecursively(sourceDir, overwrite = true)
      }
    }


    xtrasRegisterSourceTask(XtrasLibrary.TaskName.BUILD, target) {
      inputs.files(fileTree(goDir) {
        include("**/*.go")
        include("**/*.c")
        include("**/*.h")
        include("**/*.mod")
      })

      ssl.addBuildFlags(target, environment)
      environment["GOARCH"] = target.goArch
      environment["GOOS"] = target.goOS
      environment["GOARM"] = 7
      environment["CGO_CFLAGS"] = environment["CFLAGS"]
      environment["CGO_LDFLAGS"] = environment["LDFLAGS"]
      environment["CGO_ENABLED"] = 1


      val buildDir = buildDir(target)
      outputs.dir(buildDir)
      val libFile = buildDir.resolve("lib/libkipfsgo.${target.sharedLibExtn}")

      doFirst {
        println("Running go build command <${commandLine.joinToString(" ")}> in $workingDir")
      }

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
        buildEnvironment.binaries.go,
        "build",
        "-v",
        "-trimpath",
        "-buildmode=c-shared",
        "-tags=shell,openssl,node",
        "-o",
        libFile,
        modules
      )
    }


  }
}


//
//fun Project.enableGo(
//   goDir: File = file("src/go"),
//  extnName: String = "golib",
//  modules: String = "./libs",
//  config: LibraryExtension.() -> Unit = {}
//): XtrasLibrary {
//  val openSSL = enableOpenssl3()
//
//  return extensions.findByName(extnName) as? LibraryExtension
//    ?: registerLibraryExtension(extnName) {
//
//      version = project.version.toString()
//
//      //sourceDir(project.file("src/go"))
//
//
//      cinterops {
//        headers = """
//          |headers = libkipfsgo.h
//          |linkerOpts = -lkipfsgo
//          |""".trimMargin()
//      }
//
//      build { target ->
//        val buildDir = buildDir(target)
//
//        inputs.dir(goDir)
//
//
//        project.log("build{: CONFIGURING GOBUILD: $target workingDir: $workingDir goSrc: $goDir")
//        dependsOn(openSSL.extractArchiveTaskName(target))
//        workingDir(goDir)
//
//
////        inputs.files(fileTree(goDir) {
////          include("**/*.go")
////          include("**/*.c")
////          include("**/*.h")
////          include("**/*.mod")
////        })
//
//
//        openSSL.addBuildFlags(target, environment)
//        environment["GOARCH"] = target.goArch
//        environment["GOOS"] = target.goOS
//        environment["GOARM"] = 7
//        environment["CGO_CFLAGS"] = environment["CFLAGS"]
//        environment["CGO_LDFLAGS"] = environment["LDFLAGS"]
//        environment["CGO_ENABLED"] = 1
//
//
//        val libFile = buildDir.resolve("lib/libkipfsgo.${target.sharedLibExtn}")
//
//        doFirst {
//          println("Running go build command <${commandLine.joinToString(" ")}> in $workingDir")
//        }
//
//        doLast {
//          val headersDir = buildDir.resolve("include")
//          if (!headersDir.exists()) headersDir.mkdirs()
//          workingDir.resolve("libs/defs.h").copyTo(headersDir.resolve("defs.h"), true)
//          buildDir.resolve("lib/libkipfsgo.h").also {
//            it.copyTo(headersDir.resolve("libkipfsgo.h"), true)
//            it.delete()
//          }
//        }
//
//        commandLine(
//          project.binariesExtension.goBinary,
//          "build",
//          "-v",
//          "-trimpath",
//          "-buildmode=c-shared",
//          "-tags=shell,openssl,node",
//          "-o",
//          libFile,
//          modules
//        )
//      }
//
//      config()
//    }
//}
//
