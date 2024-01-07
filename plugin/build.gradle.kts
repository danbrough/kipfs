plugins {
  `kotlin-dsl`
  alias(libs.plugins.xtras)
  alias(libs.plugins.org.jetbrains.dokka)
  `maven-publish`
}

group = "org.danbrough.kipfs"

version = libs.versions.kipfs.get()

val xtrasMavenDir = if (hasProperty("xtras.dir.maven"))
  File(property("xtras.dir.maven").toString())
else if (hasProperty("xtras.dir"))
  File(property("xtras.dir").toString()).resolve("maven")
else error("Neither xtras.dir.maven or xtras.dir are set")

repositories {

  maven(xtrasMavenDir){
    name = "xtras"
  }
  maven("https://s01.oss.sonatype.org/content/groups/staging")
  mavenCentral()
  gradlePluginPortal()
  google()
}

publishing {
  repositories {

    maven(xtrasMavenDir){
      name = "xtras"
    }
  }
}

dependencies {
  implementation(libs.org.danbrough.kotlinxtras.plugin)
  compileOnly(kotlin("gradle-plugin"))
}

gradlePlugin {
  plugins {
    create("go") {
      id = "${group}.go"
      implementationClass = "$group.go.GoPlugin"
      displayName = "Go intergration plugin"
      description = "Provides integration with golang"
    }
  }
}

/*dependencies {
  compileOnly(kotlin("gradle-plugin"))
  compileOnly(kotlin("gradle-plugin-api"))
  implementation(gradleKotlinDsl())
  implementation(gradleApi())

  implementation(libs.plugin)
  implementation(libs.org.danbrough.kotlinxtras.core)
}*/

/*


*/
