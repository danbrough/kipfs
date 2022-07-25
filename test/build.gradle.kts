import Common_gradle.Common.createTarget
import Common_gradle.OpenSSL.opensslPrefix
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

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