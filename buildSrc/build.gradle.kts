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
