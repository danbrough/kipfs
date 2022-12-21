import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties

plugins {
  `kotlin-dsl`
}


group = "org.danbrough.kipfs"

val javaLangVersion = 11

val versionProps = Properties().also{
  it.load(file("../versions.properties").bufferedReader())
}

val kotlinVersion = versionProps["version.kotlin"].toString()

repositories {
  mavenCentral()
}



dependencies {
  //compileOnly(kotlin("gradle-plugin",kotlinVersion))
  implementation(kotlin("gradle-plugin"))
  compileOnly(kotlin("gradle-plugin-api"))
  compileOnly(gradleApi())
  compileOnly(gradleKotlinDsl())
  //implementation(kotlin("stdlib-common",kotlinVersion))
  implementation(kotlin("stdlib-common"))

  //compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:_")
}

/*
  implementation(kotlin("gradle-plugin", kotlinVersion))
  implementation(kotlin("serialization"))
  implementation(kotlin("stdlib-common",kotlinVersion))
  implementation(gradleApi())
  implementation(gradleKotlinDsl())

 */

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(javaLangVersion))
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(javaLangVersion))
  }
}



/*
gradlePlugin {
  plugins {

    create("goPlugin") {
      id = "$group.go"
      implementationClass = "$group.GoPlugin"
      displayName = "Go integration plugin"
      description = "Provides kotlin integration to Golang code"
    }

  }
}


*/

