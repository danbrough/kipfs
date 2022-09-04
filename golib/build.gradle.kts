import BuildEnvironment.buildEnvironment
import BuildEnvironment.platformName
import BuildEnvironment.platformNameCapitalized
import BuildEnvironment.registerTarget
import GoLib.goLibsDir
import GoLib.registerGoLibBuild
import OpenSSL.opensslPrefix
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import  org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  //id("com.android.library")
  `maven-publish`
  id("org.jetbrains.dokka")
}

kotlin {
  
  jvm {
    withJava()
  }
  
  
  val commonMain by sourceSets.getting {
    dependencies {
      implementation(project(":core"))
    }
  }
  
  val commonTest by sourceSets.getting {
    dependencies {
      implementation(kotlin("test"))
      implementation(Dependencies.coroutines_core)
    }
  }
  
  val jvmMain by sourceSets.getting {
    dependsOn(commonMain)
  }
  
  val nativeMain by sourceSets.creating {
    dependsOn(commonMain)
  }
  
  val jniMain by sourceSets.creating {
    dependsOn(nativeMain)
  }
  
  val androidNativeMain by sourceSets.creating {
    dependsOn(nativeMain)
    kotlin.srcDir(file("src/jniMain/kotlin"))
  }
  
  val nativeTest by sourceSets.creating {
    dependsOn(commonTest)
  }
  
  val goDir = project.file("src/go")
  
  val buildAll by tasks.creating
  
  BuildEnvironment.nativeTargets.forEach { target ->
    
    registerTarget(target) {
      
      val kipfsLibDir = target.goLibsDir(project)
      
      val golibBuild =
        registerGoLibBuild<KotlinNativeTarget>(
          target,
          goDir,
          kipfsLibDir,
          "kipfsgo",
          "github.com/danbrough/kipfs/libs"
        )
      
      golibBuild {
        
        buildAll.dependsOn(this)
        
        environment(target.buildEnvironment())
        
        
        appendToEnvironment(
          "CGO_CFLAGS",
          "-I${rootProject.file("openssl/lib/${target.platformName}/include")} -I${
            rootProject.file(
              "golib/src/go/libs"
            )
          }"
        )
        appendToEnvironment(
          "CGO_LDFLAGS",
          "-L${rootProject.file("openssl/lib/${target.platformName}/lib")}"
        )
        
        dependsOn(":openssl:build${target.platformNameCapitalized}")
        commandLine(commandLine.toMutableList().also {
          it.add(3, "-tags=openssl")
        })
        
        
        doFirst {
          println("STARTING KIPFS LIB BUILD... ${commandLine.joinToString(" ")}")
          println("enviroment: $environment")
          println("CGO_CFLAGS: ${environment["CGO_CFLAGS"]}")
          println("CGO_LDFLAGS: ${environment["CGO_LDLAGS"]}")
        }
        
      }
      
      val kipfsLibBuildTask = golibBuild.get()
      
      compilations["main"].apply {
        
        cinterops.create("kipfsgo") {
          packageName("kipfsgo")
          defFile = project.file("src/interop/kipfsgo.def")
          includeDirs(
            kipfsLibDir,
            rootProject.file("openssl/lib/${target.platformName}/include"),
            project.file("src/go/libs"),
          )
          
          /*
                includeDirs(
          project.buildDir.resolve("native/$platform"),
          rootProject.file("openssl/libs/$platform/include"),
          rootProject.file("go/libs")
        )
           */
          tasks.getAt(interopProcessingTaskName).apply {
            inputs.files(kipfsLibBuildTask.outputs)
            dependsOn(kipfsLibBuildTask.name)
          }
          extraOpts("-verbose")
          
          // linkerOpts("-L${opensslPrefix(platform).resolve("lib")}")
          //extraOpts("-libraryPath",opensslPrefix(platform).resolve("lib"))
        }
        
        if (target.family != Family.ANDROID) {
          cinterops.create("jni_headers") {
            packageName("platform.android")
            defFile = project.file("src/interop/jni.def")
            if (target.family.isAppleFamily) {
              includeDirs(project.file("src/include"))
              includeDirs(project.file("src/include/darwin"))
            } else if (target.family == Family.MINGW) {
              includeDirs(project.file("src/include"))
              includeDirs(project.file("src/include/win32"))
            } else if (target.family == Family.LINUX) {
              includeDirs(project.file("src/include"))
              includeDirs(project.file("src/include/linux"))
            }
          }
        }
        
        defaultSourceSet {
          if (target.family == Family.ANDROID)
            dependsOn(androidNativeMain)
          else
            dependsOn(jniMain)
        }
      }
      
      compilations["test"].apply {
        defaultSourceSet.dependsOn(nativeTest)
      }
      
      binaries {
        sharedLib("kipfs")
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

/*
android {
  compileSdk = ProjectProperties.SDK_VERSION
  sourceSets["main"].manifest.srcFile("src/AndroidManifest.xml")

  defaultConfig {
    minSdk = ProjectProperties.MIN_SDK_VERSION
    targetSdk = ProjectProperties.SDK_VERSION
  }
}*/
