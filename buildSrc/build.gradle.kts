import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

kotlinDslPluginOptions {
  jvmTarget.set(provider { java.targetCompatibility.toString() })
}

dependencies {
  compileOnly(kotlin("gradle-plugin"))
}

kotlin {
  jvmToolchain {
    check(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}


/*
tasks.withType(KotlinCompile::class) {

  kotlinOptions {
    listOf(
      "kotlin.RequiresOptIn",
      "kotlin.ExperimentalStdlibApi",
      "kotlin.ExperimentalMultiplatform",

      //  "kotlinx.serialization.InternalSerializationApi",
    //  "kotlinx.serialization.ExperimentalSerializationApi",
      // "kotlinx.coroutines.ExperimentalCoroutinesApi",
      // "kotlin.time.ExperimentalTime",
    ).map { "-Xopt-in=$it" }.also {
      freeCompilerArgs = freeCompilerArgs + it
    }
  }
}
*/

