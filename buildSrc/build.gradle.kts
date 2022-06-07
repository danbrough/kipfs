import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  //google()
}

kotlinDslPluginOptions {
  jvmTarget.set(provider { java.targetCompatibility.toString() })
//  println("COMPAT: ${java.targetCompatibility.toString()}")
}

dependencies {
  implementation(kotlin("gradle-plugin"))
  implementation(kotlin("serialization"))
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")

}

kotlin {
/*
  jvmToolchain {
    check(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(8))

  }
*/

  sourceSets.all {
    languageSettings {
      listOf(
        "kotlin.RequiresOptIn",
        "kotlin.ExperimentalStdlibApi",
        "kotlin.io.path.ExperimentalPathApi",
      ).forEach {
        optIn(it)
      }
    }
  }


}



tasks.withType(KotlinCompile::class) {
  this.kotlinOptions {
    this.jvmTarget = "11"

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

