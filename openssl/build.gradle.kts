import BuildEnvironment.buildEnvironment
import BuildEnvironment.hostTriplet
import BuildEnvironment.platformName
import BuildEnvironment.registerTarget
import OpenSSL.opensslPlatform
import OpenSSL.opensslPrefix
import OpenSSL.opensslSrcDir
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  `maven-publish`
}

val opensslGitDir = project.file("src/openssl.git")

val srcClone by tasks.registering(Exec::class) {
  commandLine(
    BuildEnvironment.gitBinary,
    "clone",
    "--bare",
    "https://github.com/openssl/openssl",
    opensslGitDir
  )
  outputs.dir(opensslGitDir)
  onlyIf {
    !opensslGitDir.exists()
  }
}

fun srcPrepare(target: KonanTarget): Exec =
  tasks.create("srcPrepare${target.platformName.capitalize()}", Exec::class) {
    val srcDir = target.opensslSrcDir(project)
    dependsOn(srcClone)
    onlyIf {
      !srcDir.exists()
    }
    commandLine(
      BuildEnvironment.gitBinary, "clone", "--branch", OpenSSL.TAG, opensslGitDir, srcDir
    )
  }


fun configureTask(target: KonanTarget): Exec {
  
  val srcPrepare = srcPrepare(target)
  
  return tasks.create("configure${target.platformName.capitalize()}", Exec::class) {
    dependsOn(srcPrepare)
    workingDir(target.opensslSrcDir(project))
    environment(target.buildEnvironment())
    doFirst {
      println("ENVIRONMENT: ${environment}")
    }
    val args = mutableListOf(
      "./Configure", target.opensslPlatform,
      "--prefix=${target.opensslPrefix(project)}", "no-tests", "no-posix-io",
      //"no-tests","no-ui-console", "--prefix=${target.opensslPrefix(project)}"
    )
    if (target.family == Family.ANDROID) args += "-D__ANDROID_API__=${BuildEnvironment.androidNdkApiVersion} "
    else if (target.family == Family.MINGW) args += "--cross-compile-prefix=${target.hostTriplet}-"
    commandLine(args)
  }
}


fun buildTask(target: KonanTarget): TaskProvider<*> {
  val configureTask = configureTask(target)
  
  
  return tasks.register("build${target.platformName.capitalized()}", Exec::class) {
    target.opensslPrefix(project).resolve("lib/libssl.a").exists().also {
      isEnabled = !it
      configureTask.isEnabled = !it
    }
    dependsOn(configureTask.name)
    
    
    //tasks.getAt("buildAll").dependsOn(this)
    workingDir(target.opensslSrcDir(project))
    outputs.files(fileTree(target.opensslPrefix(project)) {
      include("lib/*.a", "lib/*.so", "lib/*.h", "lib/*.dylib")
    })
    environment(target.buildEnvironment())
    group = BasePlugin.BUILD_GROUP
    commandLine("make", "install_sw")
    doLast {
      println("STATUS: $status")
      target.opensslSrcDir(project).deleteRecursively()
    }
    
  }
}

kotlin {
  
  val commonTest by sourceSets.getting {
    dependencies {
      implementation(kotlin("test"))
      implementation(Dependencies.klog)
    }
  }
  
  
  val nativeTest by sourceSets.creating {
    dependsOn(commonTest)
  }
  
  val nativeMain by sourceSets.creating
  
  val buildAll by tasks.creating
  
  
  BuildEnvironment.nativeTargets.forEach { target ->
    
    registerTarget(target) {
      
      val buildTask = buildTask(target)
      buildAll.dependsOn(buildTask)
      
      
      compilations["main"].apply {
        
        cinterops.create("openssl") {
          packageName("libopenssl")
          defFile = project.file("src/openssl.def")
          includeDirs(konanTarget.opensslPrefix(project).resolve("include"))
          extraOpts(listOf("-libraryPath", konanTarget.opensslPrefix(project).resolve("lib"),"-verbose"))
        }
        
        defaultSourceSet {
          dependsOn(nativeMain)
        }
      }
      
      
      compilations["test"].apply {
        defaultSourceSet {
          dependsOn(nativeTest)
        }
      }
      
      binaries {
        sharedLib("kipfsopenssl", buildTypes = setOf(NativeBuildType.DEBUG)) {
        
        }
      }
    }
    
  }
}

tasks.create("nativeTargets") {
  doLast {
    println("nativeTargets: ${BuildEnvironment.nativeTargets}")
  }
}

