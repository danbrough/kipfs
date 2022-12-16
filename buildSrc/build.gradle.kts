//import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(kotlin("gradle-plugin"))

}

repositories {
  mavenCentral()
}

val javaLangVersion = 11

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(javaLangVersion))
  //sourceCompatibility = JavaVersion.VERSION_11
}


kotlin {
  jvmToolchain {
    check(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(javaLangVersion))
  }
}

/*
tasks.withType<KotlinJvmCompile> {
  kotlinOptions {
    jvmTarget = javaLangVersion.toString()
  }
}
*/



