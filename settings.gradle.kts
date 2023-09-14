import org.gradle.kotlin.dsl.extra


pluginManagement {
  repositories {
    maven("/usr/local/xtras/maven")
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    gradlePluginPortal()
    mavenCentral()
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


