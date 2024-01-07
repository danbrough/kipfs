import org.gradle.kotlin.dsl.extra


pluginManagement {
  repositories {
    val xtrasMavenDir = settings.extra.properties.let { properties ->
      properties.getOrDefault("xtras.dir.maven", null)?.toString()
        ?: properties.getOrDefault("xtras.dir", null)
          ?.toString()?.let { File(it).resolve("maven").absolutePath }
        ?: error("Gradle property xtras.dir is not set.")
    }

    maven(xtrasMavenDir)
    maven("https://s01.oss.sonatype.org/content/groups/staging")
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}





plugins {
  id("de.fayard.refreshVersions") version "0.60.2"
  id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}


rootProject.name = "kipfs"

val include: String? by settings

include(":core")

includeBuild("plugin")


if (include == null || include == "golib")
  include(":golib")


//include(":test")
/*if (publish == null || publish == "plugin") {
  include(":plugin")
}*/


//if (!pluginsOnly.toBoolean()) {
//  include(":core")
//  include(":golib")
//  //include(":demo")
//}


File("/tmp/gradle_settings.log").appendText(
  gradle.startParameter.taskNames.joinToString("\n") {
    "SETTINGS TASK: $it"
  }
)
