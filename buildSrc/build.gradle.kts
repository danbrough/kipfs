import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}


val javaVersion = JavaVersion.VERSION_11

java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

kotlinDslPluginOptions {
  jvmTarget.set(provider { java.targetCompatibility.toString() })
}

tasks.withType(KotlinCompile::class) {

  kotlinOptions {
    listOf(
      "kotlin.RequiresOptIn",
      "kotlin.ExperimentalStdlibApi",
      //  "kotlinx.serialization.InternalSerializationApi",
    //  "kotlinx.serialization.ExperimentalSerializationApi",
      // "kotlinx.coroutines.ExperimentalCoroutinesApi",
      // "kotlin.time.ExperimentalTime",
    ).map { "-Xopt-in=$it" }.also {

      freeCompilerArgs = freeCompilerArgs + it
    }
  }
}

