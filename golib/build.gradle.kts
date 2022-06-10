import Common_gradle.Common.createTarget
import Common_gradle.GoLib.libsDir
import Common_gradle.GoLib.registerGoLibBuild
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  id("common")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME



kotlin {
  jvm {
    withJava()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(AndroidUtils.logging)
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

    val jvmMain by getting {
      dependsOn(commonMain)
    }
  }


  val goDir = project.file("src/go")

  BuildEnvironment.nativeTargets.forEach { platform ->

    createTarget(platform) {

      val kipfsLibDir = libsDir(platform)
      val kipfsLibBuildTaskProvider = registerGoLibBuild(platform, goDir, kipfsLibDir, "kipfsgo", "libs/libkipfs.go")

      kipfsLibBuildTaskProvider {
        doFirst {
          println("STARTING KIPFS LIB BUILD... ${commandLine.joinToString(" ")}")
          println("CGO_CFLAGS: ${environment["CGO_CFLAGS"]}")
          println("CGO_LDFLAGS: ${environment["CGO_LDLAGS"]}")
        }

        appendToEnvironment("CGO_CFLAGS", "-I${rootProject.file("openssl/build/openssl/$platform/include")}")
        appendToEnvironment("CGO_LDFLAGS", "-L${rootProject.file("openssl/build/openssl/$platform/lib")}")

        dependsOn(":openssl:build${platform.name.toString().capitalized()}")
        commandLine(commandLine.toMutableList().also {
          it.add(3, "-tags=openssl")
        })

      }

      val kipfsLibBuildTask = kipfsLibBuildTaskProvider.get()


      //println("TARGET: ${this.konanTarget.family} PRESET_NAME: $name")


      compilations["main"].apply {

        cinterops.create("kipfs") {
          packageName("kipfs")
          defFile = project.file("src/interop/kipfs.def")
          includeDirs(
            kipfsLibDir,
            project.file("src/include"),
            project.file("src/go/libs"),
          )
          tasks.getAt(interopProcessingTaskName).apply {
            inputs.files(kipfsLibBuildTask.outputs)
            dependsOn(kipfsLibBuildTask.name)
          }
        }

        if (platform.goOS != GoOS.android) {
          cinterops.create("jni") {
            packageName("platform.android")
            defFile = project.file("src/interop/jni.def")
            includeDirs(project.file("src/include"))
            when (platform.goOS) {
              GoOS.linux -> {
                includeDirs(project.file("src/include/linux"))
              }
              GoOS.windows -> {
                includeDirs(project.file("src/include/win32"))
              }
              else -> {
                TODO("add other jni headers")
              }
            }
          }
        }

        defaultSourceSet {
          dependsOn(sourceSets["nativeMain"])
        }
      }


      binaries {

        /*       executable("demo") {
                 if (konanTarget.family == Family.ANDROID) {
                   binaryOptions["androidProgramType"] = "nativeActivity"
                 }

                 runTask?.environment("LD_LIBRARY_PATH", kipfsLibDir)
               }*/

        sharedLib("kipfs", setOf(NativeBuildType.DEBUG))

      }


    }
  }


}

tasks.withType(KotlinNativeTest::class).all {
  environment("LD_LIBRARY_PATH", libsDir(BuildEnvironment.hostPlatform))
}


tasks.withType(KotlinJvmTest::class) {
  val linkTask = tasks.getByName("linkKipfsDebugSharedLinuxX64")
  dependsOn(linkTask)
  val libPath =
    "${libsDir(BuildEnvironment.hostPlatform)}${File.pathSeparator}${linkTask.outputs.files.files.first()}"
  println("LIBPATH: $libPath")
  environment(
    "LD_LIBRARY_PATH",
    libPath
  )
}


tasks.register("printPresets"){
  kotlin.presets.all {
    println(name)
  }
}