plugins {
  `kotlin-dsl`
  `maven-publish`
  id("org.jetbrains.dokka")
  id("org.danbrough.kotlinxtras.binaries")
  id("org.danbrough.kotlinxtras.sonatype")
}

dependencies {
  compileOnly(kotlin("gradle-plugin"))
  compileOnly(kotlin("gradle-plugin-api"))
  implementation("org.danbrough.kotlinxtras:plugin:_")
  implementation("org.danbrough.kotlinxtras:core:_")
}

gradlePlugin {
  plugins {
    create("go") {
      id = "${group}.go"
      implementationClass = "$group.GoPlugin"
      displayName = "Go intergration plugin"
      description = "Provides integration with golang"
    }
  }
}

