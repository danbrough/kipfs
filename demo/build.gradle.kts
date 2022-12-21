import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")

  id("org.danbrough.kotlinxtras.core")
}

kotlin {
  linuxX64()

  val commonMain by sourceSets.getting {
    dependencies {
      implementation(project(":golib"))
      implementation("org.danbrough:klog:_")
    }
  }

  val posixMain by sourceSets.creating {
    dependsOn(commonMain)
  }

  targets.withType(KotlinNativeTarget::class) {

    compilations["main"].apply {
      defaultSourceSet.dependsOn(posixMain)
    }

    binaries {
      executable("golibDemo") {
        entryPoint("demo.main")
      }
    }
  }


}