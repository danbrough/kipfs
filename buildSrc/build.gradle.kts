import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  //google()
}


val props = Properties().apply {
  file("../versions.properties").inputStream().use { load(it) }
}

val kotlinVersion: String = props.getProperty("version.kotlin")

kotlinDslPluginOptions {
  jvmTarget.set(provider { java.targetCompatibility.toString() })
}

dependencies {
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
  implementation(kotlin("gradle-plugin", kotlinVersion))
  implementation(kotlin("serialization"))
  implementation(gradleApi())
  implementation(gradleKotlinDsl())
  
  
}

kotlin {
  
  jvmToolchain {
    check(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(11))
  }
  
  
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
  kotlinOptions {
    jvmTarget = "11"
  }
}

