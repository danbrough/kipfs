import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")

}

kotlin {
  linuxX64()
  macosX64()


  val nativeTest by sourceSets.creating {
    dependencies {
      implementation(kotlin("test"))
    }
  }

  targets.withType(KotlinNativeTarget::class).all {
    compilations["test"].apply {
      defaultSourceSet.dependsOn(nativeTest)
    }
  }
}