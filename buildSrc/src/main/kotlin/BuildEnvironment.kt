@file:Suppress("MemberVisibilityCanBePrivate")

import BuildEnvironment.goArch
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import org.jetbrains.kotlin.konan.target.Architecture
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File


object BuildEnvironment {
  
  val goBinary: String by ProjectProperties.createProperty("go.binary", "/usr/bin/go")
  
  val gitBinary: String by ProjectProperties.createProperty("git.binary", "/usr/bin/git")
  
  val buildCacheDir: File by ProjectProperties.createProperty("build.cache")
  
  val goCacheDir: File by lazy {
    buildCacheDir.resolve("go")
  }
  
  val konanDir: File by ProjectProperties.createProperty(
    "konan.dir", "${System.getProperty("user.home")}/.konan"
  )
  
  val androidNdkDir: File by ProjectProperties.createProperty("android.ndk.dir")
  
  val androidNdkApiVersion: Int by ProjectProperties.createProperty("android.ndk.api.version", "23")
  
  private val buildPathList: String by ProjectProperties.createProperty("build.path")
  
  val buildPath: List<String>
    get() = buildPathList.split("[\\s]+".toRegex())
  
  val KonanTarget.platformName: String
    get() = name.split("_").joinToString("") { it.capitalize() }.decapitalize()
  
  
  val KonanTarget.hostTriple: String
    get() = when (this) {
      KonanTarget.LINUX_ARM64 -> "aarch64-unknown-linux-gnu"
      KonanTarget.LINUX_X64 -> "x86_64-unknown-linux-gnu"
      KonanTarget.ANDROID_ARM32 -> "armv7a-linux-androideabi"
      KonanTarget.ANDROID_ARM64 -> "aarch64-linux-android"
      KonanTarget.ANDROID_X64 -> "x86_64-linux-android"
      KonanTarget.ANDROID_X86 -> "i686-linux-android"
      KonanTarget.IOS_ARM32 -> TODO()
      KonanTarget.IOS_ARM64 -> TODO()
      KonanTarget.IOS_SIMULATOR_ARM64 -> TODO()
      KonanTarget.IOS_X64 -> TODO()
      KonanTarget.LINUX_ARM32_HFP -> TODO()
      KonanTarget.LINUX_MIPS32 -> TODO()
      KonanTarget.LINUX_MIPSEL32 -> TODO()
      KonanTarget.MACOS_ARM64 -> TODO()
      KonanTarget.MACOS_X64 -> "darwin64-x86_64-cc"
      KonanTarget.MINGW_X64 -> "x86_64-w64-mingw32"
      KonanTarget.MINGW_X86 -> TODO()
      KonanTarget.TVOS_ARM64 -> TODO()
      KonanTarget.TVOS_SIMULATOR_ARM64 -> TODO()
      KonanTarget.TVOS_X64 -> TODO()
      KonanTarget.WASM32 -> TODO()
      KonanTarget.WATCHOS_ARM32 -> TODO()
      KonanTarget.WATCHOS_ARM64 -> TODO()
      KonanTarget.WATCHOS_SIMULATOR_ARM64 -> TODO()
      KonanTarget.WATCHOS_X64 -> TODO()
      KonanTarget.WATCHOS_X86 -> TODO()
      else -> TODO("Add hostTriple for $this")
      
    }
  
  val KonanTarget.androidLibDir: String?
    get() = when (this) {
      KonanTarget.ANDROID_ARM32 -> "armeabi-v7a"
      KonanTarget.ANDROID_ARM64 -> "arm64-v8a"
      KonanTarget.ANDROID_X64 -> "x86_64"
      KonanTarget.ANDROID_X86 -> "x86"
      else -> null
    }
  
  val hostTarget: KonanTarget
    get() {
      val osName = System.getProperty("os.name")
      val osArch = System.getProperty("os.arch")
      val hostArchitecture: Architecture = when (osArch) {
        "amd64", "x86_64" -> Architecture.X64
        "arm64", "aarch64" -> Architecture.ARM64
        else -> throw Error("Unknown os.arch value: $osArch")
      }
      
      return when {
        osName == "Linux" -> {
          when (hostArchitecture) {
            Architecture.ARM64 -> KonanTarget.LINUX_ARM64
            Architecture.X64 -> KonanTarget.LINUX_X64
            else -> null
          }
        }
        
        osName.startsWith("Mac") -> {
          when (hostArchitecture) {
            Architecture.X64 -> KonanTarget.MACOS_X64
            Architecture.ARM64 -> KonanTarget.MACOS_ARM64
            else -> null
          }
        }
        
        osName.startsWith("Windows") -> {
          when (hostArchitecture) {
            Architecture.X64 -> KonanTarget.MINGW_X64
            else -> null
          }
        }
        else -> null
      } ?: throw Error("Unknown build host: $osName:$osArch")
    }
  
  val nativeTargets: List<KonanTarget> =
    if (ProjectProperties.IDE_ACTIVE) listOf(hostTarget) else listOf(
      KonanTarget.LINUX_X64, KonanTarget.MACOS_X64
    )
  
  
  val androidToolchainDir by lazy {
    androidNdkDir.resolve("toolchains/llvm/prebuilt/linux-x86_64").also {
      assert(it.exists()) {
        "Failed to locate ${it.absolutePath}"
      }
    }
  }
  
  val clangBinDir by lazy {
    File("$konanDir/dependencies").listFiles()?.first {
      it.isDirectory && it.name.contains("essentials")
    }?.let { it.resolve("bin") }
      ?: throw Error("Failed to locate clang folder in ${konanDir}/dependencies")
  }
  
  
  /*
  see:   go/src/go/build/syslist.go
  const goosList = "aix android darwin dragonfly freebsd hurd illumos
  ios js linux nacl netbsd openbsd plan9 solaris windows zos "
  const goarchList = "386 amd64 amd64p32 arm armbe arm64
  arm64be loong64 mips mipsle mips64 mips64le mips64p32 mips64p32le ppc
   ppc64 ppc64le riscv riscv64 s390 s390x sparc sparc64 wasm "
    */
  val KonanTarget.goOS: String?
    get() = when (family) {
      Family.OSX -> "darwin"
      Family.IOS, Family.TVOS, Family.WATCHOS -> "ios"
      Family.LINUX -> "linux"
      Family.MINGW -> "windows"
      Family.ANDROID -> "android"
      Family.WASM -> null
      Family.ZEPHYR -> null
    }
  
  val KonanTarget.goArch: String
    get() = when (architecture) {
      Architecture.ARM64 -> "arm64"
      Architecture.X64 -> "amd64"
      Architecture.X86 -> "386"
      Architecture.ARM32 -> "arm"
      Architecture.MIPS32 -> "mips" //TODO: confirm this
      Architecture.MIPSEL32 -> "mipsle" //TODO: confirm this
      Architecture.WASM32 -> "wasm"
    }
  
  fun KonanTarget.buildEnviroment(): Map<String, Any?> = mutableMapOf(
    "CGO_ENABLED" to 1, "GOARM" to 7, "GOOS" to goOS, "GOARCH" to goArch,
    "GOBIN" to buildCacheDir.resolve("$name/bin"),
    "GOCACHE" to buildCacheDir.resolve("$name/gobuild"),
    "GOCACHEDIR" to buildCacheDir.resolve("$name/gocache"),
    "GOMODCACHE" to buildCacheDir.resolve("gomodcache"),
    "GOPATH" to buildCacheDir.resolve(name),
    "KONAN_DATA_DIR" to buildCacheDir.resolve("konan"),
    "CFLAGS" to "-O3  -Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1",
    "MAKE" to "make -j4",
  ).apply {
    val path = buildPath.toMutableList()
    
    when (this@buildEnviroment) {
      
      KonanTarget.LINUX_ARM32_HFP -> {
        val clangArgs =
          "--target=$hostTriple --gcc-toolchain=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2 --sysroot=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot "
        this["CC"] = "$clangBinDir/clang $clangArgs"
        this["CXX"] = "$clangBinDir/clang++ $clangArgs"
      }
      
      KonanTarget.LINUX_ARM64 -> {
        val clangArgs =
          "--target=$hostTriple --gcc-toolchain=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 --sysroot=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot"
        this["CC"] = "$clangBinDir/clang $clangArgs"
        this["CXX"] = "$clangBinDir/clang++ $clangArgs"
      }
      
      KonanTarget.LINUX_X64 -> {
        val clangArgs =
          "--target=$hostTriple --gcc-toolchain=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2 --sysroot=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot"
        this["CC"] = "$clangBinDir/clang $clangArgs"
        this["CXX"] = "$clangBinDir/clang++ $clangArgs"
/*        this["RANLIB"] =
          "$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin/ranlib"*/
      }
      
      KonanTarget.MACOS_X64 -> {
      
      }
      
      
      KonanTarget.MINGW_X64 -> {
        
        /*  export HOST=x86_64-w64-mingw32
  export GOOS=windows
  export CFLAGS="$CFLAGS -pthread"
  #export WINDRES=winres
  export WINDRES=/usr/bin/x86_64-w64-mingw32-windres
  export RC=$WINDRES
  export GOARCH=amd64
  export OPENSSL_PLATFORM=mingw64
  export LIBNAME="libkipfs.dll"
  #export PATH=/usr/x86_64-w64-mingw32/bin:$PATH
  export TARGET=$HOST
  #export PATH=$(dir_path bin $TOOLCHAIN):$PATH
  export CROSS_PREFIX=$TARGET-
  export CC=$TARGET-gcc
  export CXX=$TARGET-g++
        */
/*
        this["WINDRES"] = "x86_64-w64-mingw32-windres"
        this["RC"] = this["WINDRES"] as String*/
        /*this["CROSS_PREFIX"] = "${platform.host}-"
        val toolChain = "$konanDir/dependencies/msys2-mingw-w64-x86_64-1"
        this["PATH"] = "$toolChain/bin:${this["PATH"]}"*/
        
        this["CC"] = "gcc"
        this["CXX"] = "g++"
        
        
      }
      
      KonanTarget.ANDROID_X64, KonanTarget.ANDROID_X86, KonanTarget.ANDROID_ARM64, KonanTarget.ANDROID_ARM32 -> {
        path.add(0, androidToolchainDir.resolve("bin").absolutePath)
        this["CC"] = "$hostTriple${androidNdkApiVersion}-clang"
        this["CXX"] = "$hostTriple${androidNdkApiVersion}-clang++"
        this["AR"] = "llvm-ar"
        this["RANLIB"] = "llvm-ranlib"
      }
    }
    
    this["PATH"] = path.joinToString(File.pathSeparator)
  }
  
  
}

