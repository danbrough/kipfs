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
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
  implementation(kotlin("gradle-plugin","1.7.0"))
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

