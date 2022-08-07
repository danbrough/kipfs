import BuildEnvironment.platformName
import BuildEnvironment.platformNameCapitalized
import BuildEnvironment.registerTarget
import GoLib.goLibsDir
import GoLib.registerGoLibBuild
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import  org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
}


group = ProjectProperties.projectGroup
version = ProjectProperties.buildVersionName
println("IDE ACTIVE: ${ProjectProperties.IDE_ACTIVE}")


tasks.create("dude") {
  
  doLast {
    System.getProperties().also {
      it.keys.forEach { key ->
        println("PROP: $key -> ${it[key]}")
      }
    }
    
    println("IDEA ACTIVE: ${ProjectProperties.IDE_ACTIVE}")
  }
}

kotlin {
  jvm {
    withJava()
  }
  
  
  val commonMain by sourceSets.getting {
    dependencies {
      implementation(project(":api"))
    }
  }
  
  
  val commonTest by sourceSets.getting {
    dependencies {
      implementation(kotlin("test"))
      api("io.matthewnelson.kotlin-components:encoding-base32:_")
    }
  }
  
  
  val jvmMain by sourceSets.getting {
    dependsOn(commonMain)
  }
  
  
  val nativeMain by sourceSets.creating {
    dependsOn(commonMain)
  }
  
  val nativeTest by sourceSets.creating {
    dependsOn(commonTest)
  }
  
  val goDir = project.file("src/go")
  val buildAll by tasks.creating
  
  BuildEnvironment.nativeTargets.forEach { target ->
    
    
    println("REGISTERING $target")
    registerTarget(target) {
      
      
      val kipfsLibDir = target.goLibsDir(project)
      
      val golibBuild =
        registerGoLibBuild<KotlinNativeTarget>(
          target,
          goDir,
          kipfsLibDir,
          "kipfsgo",
          "libs/libkipfs.go"
        )
      
      
      
      golibBuild {
        
        buildAll.dependsOn(this)
        
        doFirst {
          println("STARTING KIPFS LIB BUILD... ${commandLine.joinToString(" ")}")
          println("CGO_CFLAGS: ${environment["CGO_CFLAGS"]}")
          println("CGO_LDFLAGS: ${environment["CGO_LDLAGS"]}")
        }
        
        appendToEnvironment(
          "CGO_CFLAGS",
          "-I${rootProject.file("openssl/lib/${target.platformName}/include")}"
        )
        appendToEnvironment(
          "CGO_LDFLAGS",
          "-L${rootProject.file("openssl/lib/${target.platformName}/lib")}"
        )
        
        dependsOn(":openssl:build${target.platformName.capitalized()}")
        commandLine(commandLine.toMutableList().also {
          it.add(3, "-tags=openssl")
        })
      }
      
      val kipfsLibBuildTask = golibBuild.get()
      
      
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
          // linkerOpts("-L${opensslPrefix(platform).resolve("lib")}")
          //extraOpts("-libraryPath",opensslPrefix(platform).resolve("lib"))
        }
        
        if (target.family != Family.ANDROID) {
          cinterops.create("jni") {
            packageName("platform.android")
            defFile = project.file("src/interop/jni.def")
            includeDirs(project.file("src/include"))
            if (target.family.isAppleFamily) {
              includeDirs(project.file("src/include/darwin"))
            } else if (target.family == org.jetbrains.kotlin.konan.target.Family.MINGW) {
              includeDirs(project.file("src/include/win32"))
            } else {
              includeDirs(project.file("src/include/linux"))
            }
          }
        }
        
        defaultSourceSet {
          dependsOn(nativeMain)
        }
      }
      
      compilations["test"].defaultSourceSet.dependsOn(nativeTest)
      
      
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
  environment(
    if (BuildEnvironment.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH",
    BuildEnvironment.hostTarget.goLibsDir(project)
  )
}


tasks.withType(KotlinJvmTest::class) {
  
  val linkTask =
    tasks.getByName("linkKipfsDebugShared${BuildEnvironment.hostTarget.platformNameCapitalized}")
  dependsOn(linkTask)
  
  
  environment(
    if (BuildEnvironment.hostIsMac) "DYLD_LIBRARY_PATH" else "LD_LIBRARY_PATH",
    "${BuildEnvironment.hostTarget.goLibsDir(project)}${File.pathSeparator}${linkTask.outputs.files.files.first()}"
  )
}


tasks.register("printPresets") {
  kotlin.presets.all {
    println(name)
  }
}