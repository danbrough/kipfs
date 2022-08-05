import BuildEnvironment.buildEnviroment
import BuildEnvironment.platformName
import Common_gradle.Common.createTarget
import OpenSSL.opensslPlatform
import OpenSSL.opensslPrefix
import OpenSSL.opensslSrcDir
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  id("common")
  `maven-publish`
}

group = ProjectProperties.projectGroup
version = ProjectProperties.buildVersionName

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
    val srcDir = target.opensslSrcDir
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
  
  return tasks.create(
    "configure${target.platformName.capitalize()}", Exec::class
  ) {
    dependsOn(srcPrepare)
    workingDir(target.opensslSrcDir)
    println("configuring with platform: ${target.opensslPlatform}")
    environment(target.buildEnviroment())
    val args = mutableListOf(
      "./Configure", target.opensslPlatform,
      //"no-shared",
      "no-tests", "--prefix=${target.opensslPrefix(project)}"
    )
    if (target.family == Family.ANDROID) args += "-D__ANDROID_API__=${BuildEnvironment.androidNdkApiVersion} "
    else if (target.family == Family.MINGW) args += "--cross-compile-prefix=${target.hostTriple}-"
    commandLine(args)
  }
}

fun buildTask(target: PlatformNative<*>) {
  val configureTask = configureTask(platform)
  
  tasks.create("build${platform.name.toString().capitalized()}", Exec::class) {
    
    opensslPrefix(platform).resolve("lib/libssl.a").exists().also {
      isEnabled = !it
      configureTask.isEnabled = !it
    }
    dependsOn(configureTask.name)
    
    
    tasks.getAt("buildAll").dependsOn(this)
    workingDir(platform.opensslSrcDir)
    outputs.files(fileTree(opensslPrefix(platform)) {
      include("lib/*.a", "lib/*.so", "lib/*.h", "lib/*.dylib")
    })
    environment(BuildEnvironment.environment(platform))
    group = BasePlugin.BUILD_GROUP
    commandLine("make", "install_sw")
    doLast {
      platform.opensslSrcDir.deleteRecursively()
    }
    
  }
}


kotlin {
  
  val buildAll by tasks.registering
  val commonTest by sourceSets.getting {
    dependencies {
      implementation(kotlin("test"))
    }
  }
  
  val nativeTest by sourceSets.creating
  val nativeMain by sourceSets.creating
  
  linuxX64()
  linuxArm64()
  macosX64()
  macosArm64()
  
  
  BuildEnvironment.nativeTargets.forEach { platform ->
    
    createTarget(platform) {
      compilations["main"].apply {
        
        cinterops.create("openssl") {
          packageName("libopenssl")
          defFile = project.file("src/openssl.def")
          extraOpts(listOf("-libraryPath", opensslPrefix(platform).resolve("lib")))
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

/*      binaries {
        executable("testApp") {
          entryPoint = "openssl.TestApp"
        }
      }*/
    }
    
    buildTask(platform)
  }
}


publishing {
  publications {
  }
  
  repositories {
    maven(ProjectProperties.LOCAL_M2)
  }
}


/*
echo OPENSSL is $OPENSSL
CRYPTO_LIB=$OPENSSL/lib/libcrypto.a

if [ -f $CRYPTO_LIB ]; then
  echo not building openssl as $CRYPTO_LIB exists
else
  echo OPENSSL_PLATFORM $OPENSSL_PLATFORM
  echo OPENSSL $OPENSSL
  echo CC $CC CXX: $CXX
  echo CFLAGS $CFLAGS
  echo SYSROOT $SYSROOT
  echo CROSS_PREFIX $CROSS_PREFIX
  echo ANDROID_API $ANDROID_API
  sleep 2

  clean_src
  cd $SRC

  if [ "$GOOS" == "android" ]; then
    ./Configure $OPENSSL_PLATFORM no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$OPENSSL" $EXTRAS || exit 1
  else
    ./Configure --prefix="$OPENSSL" $OPENSSL_PLATFORM   $EXTRAS || exit 1
  fi
  make install_sw || exit 1
fi
 */
