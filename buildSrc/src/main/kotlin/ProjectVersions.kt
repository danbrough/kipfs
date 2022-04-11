import java.util.*
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.net.URI

object ProjectVersions {
  const val SDK_VERSION = 32
  const val MIN_SDK_VERSION = 23
  const val BUILD_TOOLS_VERSION = "32.0.0"
  val JAVA_VERSION = JavaVersion.VERSION_11
  var VERSION_FORMAT = "0.0.1-%02d"
  const val KOTLIN_JVM_VERSION = "11"

  var IDE_MODE = false

  lateinit var KEYSTORE_PASSWORD: String
  val COMPOSE_VERSION = "1.1.1"
  val JITPACK_BUILD = System.getenv().containsKey("JITPACK")
  val NDK_VERSION = if (JITPACK_BUILD) "21.1.6352462" else "23.1.7779620"
  var BUILD_VERSION = 1
  var VERSION_OFFSET = 1
  lateinit var MAVEN_REPO: URI

  var GROUP_ID = "com.github.danbrough.kipfs"

  val VERSION_NAME: String
    get() = getVersionName()

  fun init(project: Project, props: Properties) {
    IDE_MODE = project.findProperty("kipfs.ideMode")?.toString()?.trim()?.toBoolean() ?: true
    BUILD_VERSION = props.getProperty("buildVersion", "1").toInt()
    VERSION_OFFSET = props.getProperty("versionOffset", "1").toInt()
    VERSION_FORMAT = props.getProperty("versionFormat", "0.0.%d").trim()
    KEYSTORE_PASSWORD = project.properties.get("KEYSTORE_PASSWORD")?.toString() ?: ""
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
