import BuildEnvironment.buildEnvironment
import BuildEnvironment.hostTriplet
import BuildEnvironment.platformName
import BuildEnvironment.registerTarget
import OpenSSL.opensslPlatform
import OpenSSL.opensslPrefix
import OpenSSL.opensslSrcDir
import Curl.curlSrcDir
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  `maven-publish`
}

val curlGitDir = project.file("src/curl.git")

val srcClone by tasks.registering(Exec::class) {
  commandLine(
    BuildEnvironment.gitBinary,
    "clone",
    "--bare",
    "https://github.com/curl/curl.git",
    curlGitDir
  )
  outputs.dir(curlGitDir)
  onlyIf {
    !curlGitDir.exists()
  }
}

fun srcPrepare(target: KonanTarget): Exec =
  tasks.create("srcPrepare${target.platformName.capitalize()}", Exec::class) {
    val srcDir = target.curlSrcDir(project)
    dependsOn(srcClone)
    onlyIf {
      !srcDir.exists()
    }
    commandLine(
      BuildEnvironment.gitBinary, "clone", "--branch", Curl.TAG, curlGitDir, srcDir
    )
  }


fun configureTask(target: KonanTarget): Exec {
  
  val srcPrepare = srcPrepare(target)
  
  return tasks.create("configure${target.platformName.capitalize()}", Exec::class) {
    dependsOn(srcPrepare)
    workingDir(target.curlSrcDir(project))
    environment(target.buildEnvironment())
    doFirst {
      println("ENVIRONMENT: ${environment}")
    }
    
    
    

    val args = listOf("date")
    commandLine(args)
  }
}

kotlin {
  linuxX64()
  
  val commonMain by sourceSets.getting {
    dependencies {
      implementation(Dependencies.klog)
    }
  }
  
  val posixMain by sourceSets.creating {
    dependsOn(commonMain)
  }
  
  targets.withType(KotlinNativeTarget::class).all {
  
    configureTask(konanTarget)
    
    compilations["main"].apply {
      defaultSourceSet.dependsOn(posixMain)
    }
  }
  
  
}