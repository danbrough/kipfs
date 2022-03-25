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
  //jvmTarget.set(java.toolchain.languageVersion.map { it.toString() })
  jvmTarget.set(provider { java.targetCompatibility.toString() })
}
