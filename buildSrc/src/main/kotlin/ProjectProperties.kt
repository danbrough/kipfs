import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.FileInputStream
import java.net.URI
import java.util.*

object ProjectProperties {
  const val SDK_VERSION = 31
  const val MIN_SDK_VERSION = 23
  const val BUILD_TOOLS_VERSION = "31.0.0"
  var KOTLIN_VERSION = "1.7.0"
  val JAVA_VERSION = JavaVersion.VERSION_11
  var VERSION_FORMAT = "0.0.1-%02d"
  const val KOTLIN_JVM_VERSION = "11"

  val IDEA_ACTIVE: Boolean
    get() = System.getProperty("idea.active", "false").toBoolean()


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


  val properties = mutableMapOf<String, Any?>()
  fun getProperty(name: String): String =
    properties[name]?.toString()?.trim() ?: throw Error("property $name not specified")

  fun getProperty(name: String, default: String): String =
    if (properties.containsKey(name))
      properties[name]!!.toString().trim() else default

  private var isInitialized = false
  fun init(_project: Project) {

    // println("INIT PROJECT PROPERTIES")
    if (isInitialized) {
      return
    }
    isInitialized = true


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


    BUILD_VERSION = getProperty("buildVersion", "1").toInt()
    VERSION_OFFSET = getProperty("versionOffset", "1").toInt()
    VERSION_FORMAT = getProperty("versionFormat", "0.0.%d")
    KEYSTORE_PASSWORD = getProperty("KEYSTORE_PASSWORD", "")

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



