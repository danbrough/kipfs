import Common_gradle.Common.createTarget
import Common_gradle.GoLib.libsDir
import Common_gradle.GoLib.registerGoLibBuild
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

      val goLibDir = libsDir(platform)
      val golibBuildTask = registerGoLibBuild(platform, goDir, goLibDir).get()

      //println("TARGET: ${this.konanTarget.family} PRESET_NAME: $name")


      compilations["main"].apply {

        cinterops.create("libgodemo") {
          packageName("godemo")
          defFile = project.file("src/interop/godemo.def")
          includeDirs(goLibDir, project.file("src/include"))
          tasks.getAt(interopProcessingTaskName).apply {
            inputs.files(golibBuildTask.outputs)
            dependsOn(golibBuildTask.name)
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

        executable("demo") {
          if (konanTarget.family == Family.ANDROID) {
            binaryOptions["androidProgramType"] = "nativeActivity"
          }

          runTask?.environment("LD_LIBRARY_PATH", goLibDir)
        }

        sharedLib("godemojni", setOf(NativeBuildType.DEBUG))

      }


    }
  }


}

tasks.withType(KotlinNativeTest::class).all {
  environment("LD_LIBRARY_PATH", libsDir(BuildEnvironment.hostPlatform))
}


tasks.withType(KotlinJvmTest::class) {
  val linkTask = tasks.getByName("linkGodemojniDebugSharedLinuxX64")
  dependsOn(linkTask)

  environment(
    "LD_LIBRARY_PATH",
    "${libsDir(BuildEnvironment.hostPlatform)}${File.pathSeparator}${linkTask.outputs.files.files.first()}"
  )
}


