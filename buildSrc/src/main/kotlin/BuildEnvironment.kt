@file:Suppress("MemberVisibilityCanBePrivate")

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Architecture
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File
import java.util.*

object BuildEnvironment {
  
  val goBinary: String by ProjectProperties.createProperty("go.binary", "/usr/bin/go")
  
  val gitBinary: String by ProjectProperties.createProperty("git.binary", "/usr/bin/git")
  
  val buildCacheDir: File by ProjectProperties.createProperty("build.cache","/tmp/kipfscache")
  
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
  
  private fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
  
  private fun String.decapitalize(): String =
    replaceFirstChar { it.lowercase(Locale.getDefault()) }
  
  
  val KonanTarget.platformName: String
    get() {
      if (family == Family.ANDROID) {
        return when (this) {
          KonanTarget.ANDROID_X64 -> "androidNativeX64"
          KonanTarget.ANDROID_X86 -> "androidNativeX86"
          KonanTarget.ANDROID_ARM64 -> "androidNativeArm64"
          KonanTarget.ANDROID_ARM32 -> "androidNativeArm32"
          else -> throw Error("Unhandled android target $this")
        }
      }
      return name.split("_").joinToString("") { it.capitalize() }.decapitalize()
    }
  
  val KonanTarget.platformNameCapitalized: String
    get() = platformName.capitalize()
  
  
  val KonanTarget.hostTriplet: String
    get() = when (this) {
      KonanTarget.LINUX_ARM64 -> "aarch64-unknown-linux-gnu"
      KonanTarget.LINUX_X64 -> "x86_64-unknown-linux-gnu"
      KonanTarget.LINUX_ARM32_HFP -> "arm-linux-gnueabihf"
      KonanTarget.ANDROID_ARM32 -> "armv7a-linux-androideabi"
      KonanTarget.ANDROID_ARM64 -> "aarch64-linux-android"
      KonanTarget.ANDROID_X64 -> "x86_64-linux-android"
      KonanTarget.ANDROID_X86 -> "i686-linux-android"
      KonanTarget.MACOS_X64 -> "darwin64-x86_64-cc"
      KonanTarget.MINGW_X64 -> "x86_64-w64-mingw32"
/*      KonanTarget.IOS_ARM32 -> TODO()
      KonanTarget.IOS_ARM64 -> TODO()
      KonanTarget.IOS_SIMULATOR_ARM64 -> TODO()
      KonanTarget.IOS_X64 -> TODO()
      KonanTarget.LINUX_ARM32_HFP -> TODO()
      KonanTarget.LINUX_MIPS32 -> TODO()
      KonanTarget.LINUX_MIPSEL32 -> TODO()
      KonanTarget.MACOS_ARM64 -> TODO()

      KonanTarget.MINGW_X86 -> TODO()
      KonanTarget.TVOS_ARM64 -> TODO()
      KonanTarget.TVOS_SIMULATOR_ARM64 -> TODO()
      KonanTarget.TVOS_X64 -> TODO()
      KonanTarget.WASM32 -> TODO()
      KonanTarget.WATCHOS_ARM32 -> TODO()
      KonanTarget.WATCHOS_ARM64 -> TODO()
      KonanTarget.WATCHOS_SIMULATOR_ARM64 -> TODO()
      KonanTarget.WATCHOS_X64 -> TODO()
      KonanTarget.WATCHOS_X86 -> TODO()*/
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
  
  val KonanTarget.sharedLibExtn: String
    get() = when {
      family.isAppleFamily -> "dylib"
      family == Family.MINGW -> "dll"
      else -> "so"
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
  
  val hostIsMac: Boolean
    get() = hostTarget.family.isAppleFamily
  
  
  val nativeTargets: List<KonanTarget>
    get() =
      if (ProjectProperties.IDE_ACTIVE)
        listOf(hostTarget, KonanTarget.ANDROID_X86)
      else
        listOf(
          KonanTarget.LINUX_X64,
          KonanTarget.LINUX_ARM64,
          KonanTarget.LINUX_ARM32_HFP,
//          KonanTarget.MINGW_X64,
//          KonanTarget.MACOS_ARM64,
//          KonanTarget.MACOS_X64,
//          KonanTarget.ANDROID_ARM64,
//          KonanTarget.ANDROID_ARM32,
          KonanTarget.ANDROID_X64,
          KonanTarget.ANDROID_X86,
        )
  
  
  fun KotlinMultiplatformExtension.registerTarget(
    konanTarget: KonanTarget, conf: KotlinNativeTarget.() -> Unit = {}
  ): KotlinNativeTarget {
    @Suppress("UNCHECKED_CAST")
    val preset: KotlinTargetPreset<KotlinNativeTarget> =
      presets.getByName(konanTarget.platformName) as KotlinTargetPreset<KotlinNativeTarget>
    return targetFromPreset(preset, konanTarget.platformName, conf)
  }
  
  val androidToolchainDir by lazy {
    //androidNdkDir.resolve("toolchains/llvm/prebuilt/linux-x86_64").also {
    androidNdkDir.also {
      assert(it.exists()) {
        "Failed to locate ${it.absolutePath}"
      }
    }
  }
  
  /*  fun KotlinMultiplatformExtension.registerNativeTargets(conf: KotlinNativeTarget.() -> Unit) {
      nativeTargets.forEach {
        registerTarget(it, conf)
      }
    }
    */
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
  
  fun KonanTarget.buildEnvironment(): MutableMap<String, *> = mutableMapOf(
    "CGO_ENABLED" to 1, "GOARM" to 7, "GOOS" to goOS, "GOARCH" to goArch,
    "GOBIN" to buildCacheDir.resolve("$name/bin"),
    "GOCACHE" to buildCacheDir.resolve("$name/gobuild"),
    "GOCACHEDIR" to buildCacheDir.resolve("$name/gocache"),
    "GOMODCACHE" to buildCacheDir.resolve("gomodcache"),
    "GOPATH" to buildCacheDir.resolve(name),
    "KONAN_DATA_DIR" to konanDir,
    "CFLAGS" to "-O3 -pthread -Wno-macro-redefined -Wno-deprecated-declarations ",//-DOPENSSL_SMALL_FOOTPRINT=1",
    "MAKE" to "make -j4",
  ).apply {
    val path = buildPath.toMutableList()
  
    
    when (this@buildEnvironment) {
      
      KonanTarget.LINUX_ARM32_HFP -> {
        val clangArgs =
          "--target=$hostTriplet --gcc-toolchain=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2 --sysroot=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot "
        this["CC"] = "clang $clangArgs"
        this["CXX"] = "clang++ $clangArgs"
      }
      
      KonanTarget.LINUX_ARM64 -> {
        val clangArgs =
          "--target=$hostTriplet --gcc-toolchain=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 --sysroot=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot"
        this["CC"] = "clang $clangArgs"
        this["CXX"] = "clang++ $clangArgs"
      }
      
      KonanTarget.LINUX_X64 -> {
        val clangArgs =
          "--target=$hostTriplet --gcc-toolchain=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2 --sysroot=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot"
        this["CC"] = "clang $clangArgs"
        this["CXX"] = "clang++ $clangArgs"
/*        this["RANLIB"] =
          "$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin/ranlib"*/
      }
      
      KonanTarget.MACOS_X64 -> {
        this["CC"] = "gcc"
        this["CXX"] = "g++"
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
        
        this["CC"] = "x86_64-w64-mingw32-gcc"
        this["CXX"] = "x86_64-w64-mingw32-g++"
        
        
      }
      
      KonanTarget.ANDROID_X64, KonanTarget.ANDROID_X86, KonanTarget.ANDROID_ARM64, KonanTarget.ANDROID_ARM32 -> {
        path.add(0, androidToolchainDir.resolve("bin").absolutePath)
        this["CC"] = "$hostTriplet${androidNdkApiVersion}-clang"
        this["CXX"] = "$hostTriplet${androidNdkApiVersion}-clang++"
        this["AR"] = "llvm-ar"
        this["RANLIB"] = "llvm-ranlib"
      }
    }
    
    path.add(0, konanDir.resolve("dependencies/llvm-11.1.0-linux-x64-essentials/bin").absolutePath)
    this["PATH"] = path.joinToString(File.pathSeparator)
  }
}
  
  


