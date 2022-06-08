import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import java.io.File


object BuildEnvironment {

  val goBinary: String
    get() = ProjectProperties.getProperty("go.binary", "/usr/bin/go")
  val gitBinary: String
    get() = ProjectProperties.getProperty("git.binary", "/usr/bin/git")
  val javah: String
    get() = ProjectProperties.getProperty("javah.path")

  val buildCacheDir: File
    get() = File(ProjectProperties.getProperty("build.cache"))
  val konanDir: File
    get() = File(
      ProjectProperties.getProperty(
        "konan.dir", "${System.getProperty("user.home")}/.konan"
      )
    )
  val androidNdkDir: File
    get() = File(ProjectProperties.getProperty("android.ndk.dir"))
  val androidNdkApiVersion: Int
    get() = ProjectProperties.getProperty("android.ndk.api.version", "23").toInt()
  val buildPath: List<String>
    get() = ProjectProperties.getProperty("build.path").split("[\\s]+".toRegex())

  val hostPlatform = LinuxX64

  val nativeTargets: List<PlatformNative<*>>
    get() = if (ProjectProperties.IDEA_ACTIVE) listOf(LinuxX64) else listOf(
      LinuxX64,
      LinuxArm64,
      LinuxArm,
      AndroidArm,
      AndroidArm64,
      Android386,
      AndroidAmd64,
      MingwX64
    )

  val androidToolchainDir by lazy {
    androidNdkDir.resolve("toolchains/llvm/prebuilt/linux-x86_64").also {
      assert(it.exists()) {
        "Failed to locate ${it.absolutePath}"
      }
    }
  }

  val clangBinDir by lazy {
    File("$konanDir/dependencies/llvm-11.1.0-linux-x64-essentials/bin").also {
      assert(it.exists()) {
        "Failed to locate ${it.absolutePath}"
      }
    }
  }

  fun environment(platform: PlatformNative<*>): Map<String, Any> = mutableMapOf(
    "CGO_ENABLED" to 1,
    "GOOS" to platform.goOS,
    "GOARM" to platform.goArm,
    "GOARCH" to platform.goArch,
    "GOBIN" to platform.goCacheDir.resolve("${platform.name}/bin"),
    "GOCACHE" to platform.goCacheDir.resolve("${platform.name}/gobuild"),
    "GOCACHEDIR" to platform.goCacheDir,
    "GOMODCACHE" to platform.goCacheDir.resolve("mod"),
    "GOPATH" to platform.goCacheDir.resolve(platform.name.toString()),
    "KONAN_DATA_DIR" to platform.goCacheDir.resolve("konan"),
    "CFLAGS" to "-O3  -Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1",
    "MAKE" to "make -j4",
  ).apply {

    val path = buildPath.toMutableList()

    when (platform) {

      LinuxArm -> {
        val clangArgs =
          "--target=${platform.host} " + "--gcc-toolchain=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " + "--sysroot=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot "
        this["CC"] = "$clangBinDir/clang $clangArgs"
        this["CXX"] = "$clangBinDir/clang++ $clangArgs"
      }

      LinuxArm64 -> {
        val clangArgs =
          "--target=${platform.host} " + "--gcc-toolchain=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 " + "--sysroot=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot"
        this["CC"] = "$clangBinDir/clang $clangArgs"
        this["CXX"] = "$clangBinDir/clang++ $clangArgs"
      }

      LinuxX64 -> {
        val clangArgs =
          "--target=${platform.host} " + "--gcc-toolchain=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " + "--sysroot=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot"
        this["CC"] = "$clangBinDir/clang $clangArgs"
        this["CXX"] = "$clangBinDir/clang++ $clangArgs"
/*        this["RANLIB"] =
          "$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin/ranlib"*/
      }

      MingwX64 -> {

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

      AndroidArm, Android386, AndroidArm64, AndroidAmd64 -> {
        path.add(0, androidToolchainDir.resolve("bin").absolutePath)
        this["CC"] = "${platform.host}${androidNdkApiVersion}-clang"
        this["CXX"] = "${platform.host}${androidNdkApiVersion}-clang++"
        this["AR"] = "llvm-ar"
        this["RANLIB"] = "llvm-ranlib"
      }
    }

    this["PATH"] = path.joinToString(File.pathSeparator)
  }


}

enum class GoOS {
  linux, windows, android
}


enum class GoArch(val altName: String? = null) {
  x86("386"), amd64, arm, arm64;

  override fun toString() = altName ?: name
}

sealed class Platform<T : KotlinTarget>(
  val name: PlatformName,
) {
  enum class PlatformName {
    Android, AndroidNativeArm32, AndroidNativeArm64, AndroidNativeX64, AndroidNativeX86, IosArm32, IosArm64, IosSimulatorArm64, IosX64, JS, JsBoth, JsIr, Jvm, JvmWithJava, LinuxArm32Hfp, LinuxArm64, LinuxMips32, LinuxMipsel32, LinuxX64, MacosArm64, MacosX64, MingwX64, MingwX86, TvosArm64, TvosSimulatorArm64, TvosX64, Wasm, Wasm32, WatchosArm32, WatchosArm64, WatchosSimulatorArm64, WatchosX64, WatchosX86;

    override fun toString() = name.toString().decapitalize()
  }


  override fun toString() = name.toString()
}

open class PlatformNative<T : KotlinNativeTarget>(
  name: PlatformName, val host: String, val goOS: GoOS, val goArch: GoArch, val goArm: Int = 7
) : Platform<T>(name) {
  val goCacheDir: File = BuildEnvironment.buildCacheDir.resolve("go")
  val isAndroid = goOS == GoOS.android
  val isLinux = goOS == GoOS.linux
  val isWindows = goOS == GoOS.windows
}


object LinuxX64 : PlatformNative<KotlinNativeTargetWithHostTests>(
  PlatformName.LinuxX64, "x86_64-unknown-linux-gnu", GoOS.linux, GoArch.amd64
)

object LinuxArm64 : PlatformNative<KotlinNativeTarget>(
  PlatformName.LinuxArm64, "aarch64-unknown-linux-gnu", GoOS.linux, GoArch.arm64
)

object LinuxArm : PlatformNative<KotlinNativeTarget>(
  PlatformName.LinuxArm32Hfp, "arm-unknown-linux-gnueabihf", GoOS.linux, GoArch.arm
)

object MingwX64 : PlatformNative<KotlinNativeTargetWithHostTests>(
  PlatformName.MingwX64, "x86_64-w64-mingw32", GoOS.windows, GoArch.amd64
)

open class PlatformAndroid<T : KotlinNativeTarget>(
  name: PlatformName,
  host: String,
  goOS: GoOS,
  goArch: GoArch,
  goArm: Int = 7,
  val androidLibDir: String
) : PlatformNative<T>(name, host, goOS, goArch, goArm)

object AndroidArm : PlatformAndroid<KotlinNativeTarget>(
  PlatformName.AndroidNativeArm32,
  "armv7a-linux-androideabi",
  GoOS.android,
  GoArch.arm,
  androidLibDir = "armeabi-v7a"
)

object AndroidArm64 : PlatformAndroid<KotlinNativeTarget>(
  PlatformName.AndroidNativeArm64,
  "aarch64-linux-android",
  GoOS.android,
  GoArch.arm64,
  androidLibDir = "arm64-v8a",
)

object Android386 : PlatformAndroid<KotlinNativeTarget>(
  PlatformName.AndroidNativeX86,
  "i686-linux-android",
  GoOS.android,
  GoArch.x86,
  androidLibDir = "x86",
)

object AndroidAmd64 : PlatformAndroid<KotlinNativeTarget>(
  PlatformName.AndroidNativeX64,
  "x86_64-linux-android",
  GoOS.android,
  GoArch.amd64,
  androidLibDir = "x86_64",
)



