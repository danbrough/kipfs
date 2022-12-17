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

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(javaLangVersion))
}


dependencies {
  compileOnly(kotlin("gradle-plugin",kotlinVersion))
  compileOnly(kotlin("gradle-plugin-api"))
  //compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:_")
}


kotlin {

  jvmToolchain {
    check(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(javaLangVersion))
  }
}

tasks.withType<KotlinJvmCompile> {
  kotlinOptions {
    jvmTarget = javaLangVersion.toString()
  }
}


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



