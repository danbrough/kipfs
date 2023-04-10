package org.danbrough.kipfs

import org.danbrough.kotlinxtras.SHARED_LIBRARY_PATH_NAME
import org.danbrough.kotlinxtras.binaries.LibraryExtension
import org.danbrough.kotlinxtras.binaries.binariesExtension
import org.danbrough.kotlinxtras.binaries.registerLibraryExtension
import org.danbrough.kotlinxtras.binaries.sourceDir
import org.danbrough.kotlinxtras.capitalize
import org.danbrough.kotlinxtras.core.OPENSSL3_EXTN_NAME
import org.danbrough.kotlinxtras.core.enableOpenssl
import org.danbrough.kotlinxtras.core.enableOpenssl3
import org.danbrough.kotlinxtras.goArch
import org.danbrough.kotlinxtras.goOS
import org.danbrough.kotlinxtras.log
import org.danbrough.kotlinxtras.platformName
import org.danbrough.kotlinxtras.sharedLibExtn
import org.danbrough.kotlinxtras.sharedLibraryPath
import org.danbrough.kotlinxtras.xtrasLibsDir
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.KotlinTest
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File


fun Project.enableGo(
   goDir: File = file("src/go/kipfs"),
  extnName: String = "golib",
  modules: String = "./libs/",
  config: LibraryExtension.() -> Unit = {}
): LibraryExtension {
  val openSSL = enableOpenssl3()

  return extensions.findByName(extnName) as? LibraryExtension
    ?: registerLibraryExtension(extnName) {

      version = project.version.toString()

      //sourceDir(project.file("src/go"))


      cinterops {
        headers = """
          |headers = libkipfsgo.h
          |linkerOpts = -lkipfsgo 
          |""".trimMargin()
      }

      build { target ->
        val buildDir = buildDir(target)

        inputs.dir(goDir)


        project.log("build{: CONFIGURING GOBUILD: $target workingDir: $workingDir goSrc: $goDir")
        dependsOn(openSSL.extractArchiveTaskName(target))
        workingDir(goDir)


//        inputs.files(fileTree(goDir) {
//          include("**/*.go")
//          include("**/*.c")
//          include("**/*.h")
//          include("**/*.mod")
//        })


        openSSL.addBuildFlags(target, environment)
        environment["GOARCH"] = target.goArch
        environment["GOOS"] = target.goOS
        environment["GOARM"] = 7
        environment["CGO_CFLAGS"] = environment["CFLAGS"]
        environment["CGO_LDFLAGS"] = environment["LDFLAGS"]
        environment["CGO_ENABLED"] = 1
        /*        environment.also { env ->
                  env.keys.sorted().forEach {
                    println("ENV: $it\t${env[it]}")
                  }
                }*/


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
          project.binariesExtension.goBinary,
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
  override fun apply(project: Project) {
    project.afterEvaluate {
      tasks.withType(KotlinJvmTest::class.java){
        dependsOn("linkKipfsDebugShared${HostManager.host.platformName.capitalize()}")
      }
    }
  }
}




