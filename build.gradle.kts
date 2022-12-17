plugins {
  kotlin("multiplatform") apply false
  id("org.danbrough.kotlinxtras.binaries") apply false
  id("org.jetbrains.dokka") apply false

}


println("Using Kotlin compiler version: ${org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION}")


subprojects {
  repositories {
    maven("/usr/local/kotlinxtras/build/xtras/maven")
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    mavenCentral()
    google()
  }
}