import java.util.*
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.FileInputStream
import java.net.URI

object ProjectVersions {
  const val SDK_VERSION = 31
  const val MIN_SDK_VERSION = 23
  const val BUILD_TOOLS_VERSION = "31.0.0"
  val JAVA_VERSION = JavaVersion.VERSION_11
  var VERSION_FORMAT = "0.0.1-%02d"
  const val KOTLIN_JVM_VERSION = "11"

  var IDE_MODE = false

  lateinit var KEYSTORE_PASSWORD: String
  val COMPOSE_VERSION = "1.1.1"
  val JITPACK_BUILD = System.getenv().containsKey("JITPACK")
  val NDK_VERSION = "23.1.7779620"
  var BUILD_VERSION = 1
  var VERSION_OFFSET = 1
  lateinit var MAVEN_REPO: URI

  var GROUP_ID = "com.github.danbrough.kipfs"

  val VERSION_NAME: String
    get() = getVersionName()

  /*
PRESET: android
PRESET: androidNativeArm32
PRESET: androidNativeArm64
PRESET: androidNativeX64
PRESET: androidNativeX86
PRESET: iosArm32
PRESET: iosArm64
PRESET: iosSimulatorArm64
PRESET: iosX64
PRESET: js
PRESET: jsBoth
PRESET: jsIr
PRESET: jvm
PRESET: jvmWithJava
PRESET: linuxArm32Hfp
PRESET: linuxArm64
PRESET: linuxMips32
PRESET: linuxMipsel32
PRESET: linuxX64
PRESET: macosArm64
PRESET: macosX64
PRESET: mingwX64
PRESET: mingwX86
PRESET: tvosArm64
PRESET: tvosSimulatorArm64
PRESET: tvosX64
PRESET: wasm
PRESET: wasm32
PRESET: watchosArm32
PRESET: watchosArm64
PRESET: watchosSimulatorArm64
PRESET: watchosX64
PRESET: watchosX86
 */

  const val PLATFORM_LINUX_AMD64 = "linuxAmd64"
  const val PLATFORM_LINUX_ARM64 = "linuxArm64"
  const val PLATFORM_LINUX_ARM32 = "linuxArm"


  const val TARGET_HOST = "linuxAmd64"
  val TARGETS_OTHER = mapOf(
    "androidNativeArm32" to "androidArm",
    "androidNativeArm64" to "androidArm64",
    "androidNativeX64" to "androidAmd64",
    "androidNativeX86" to "android386",
    "linuxArm64" to "linuxArm64",
    "linuxArm32Hfp" to "linuxArm",
    "mingwX64" to "windowsAmd64",
  )


  val properties = mutableMapOf<String, Any?>()

  private fun getProperty(name: String, default: String?): String? =
    if (properties.containsKey(name))
      properties[name]?.toString()?.trim() else default

  fun init(_project: Project) {
    val project = _project.rootProject
    properties.putAll(project.properties)

    listOf("local.properties")
      .map { project.file(it) }
      .forEach { propFile ->
        if (propFile.exists()) {
          val props = Properties()
          FileInputStream(propFile).use {
            props.load(it)
          }
          props.forEach { key, value ->
            properties[key.toString()] = value
          }
        }
      }

    System.getProperties().forEach { key, value ->
      properties[key!!.toString()] = value
    }


    IDE_MODE = getProperty("ideMode", "true")!!.toBoolean()
    BUILD_VERSION = getProperty("buildVersion", "1")!!.toInt()
    VERSION_OFFSET = getProperty("versionOffset", "1")!!.toInt()
    VERSION_FORMAT = getProperty("versionFormat", "0.0.%d")!!.trim()
    KEYSTORE_PASSWORD = getProperty("KEYSTORE_PASSWORD", "")!!
    MAVEN_REPO = URI.create(
      project.findProperty("LOCAL_MAVEN_REPO")?.toString()?.trim()
        ?: project.rootProject.buildDir.resolve(".m2").absolutePath
    )
  }

  fun getIncrementedVersionName() = getVersionName(BUILD_VERSION + 1)

  fun getVersionName(version: Int = BUILD_VERSION) =
    VERSION_FORMAT.format(version - VERSION_OFFSET)


}


object AndroidUtils {
  private const val version = "_"
  private const val group = "com.github.danbrough.androidutils"

  const val misc = "$group:misc:$version"
  const val compose = "$group:compose:$version"
  const val permissions = "$group:permissions:$version"
  const val menu = "$group:menu:$version"
  const val logging = "$group:logging:$version"


}
