import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
  kotlin("multiplatform")
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME


kotlin {

  linuxX64("linuxAmd64")

  if (!ProjectVersions.IDE_MODE) {
    linuxArm32Hfp("linuxArm")
    androidNativeArm64("androidArm64")
    mingwX64("windowsAmd64")
  }

  val nativeMain by sourceSets.creating {

    dependencies {
      implementation(KotlinX.coroutines.core)
    }
  }

  targets.withType(KotlinNativeTarget::class).all {
    compilations["main"].apply {
      //defaultSourceSet.dependsOn(nativeMain)
      defaultSourceSet {
        dependsOn(nativeMain)

        dependencies {
          if (ProjectVersions.IDE_MODE) {
            implementation(project(":golib"))
          } else {
            implementation("com.github.danbrough.kipfs:golib:_")
          }
        }

      }
    }

    binaries {
      executable("kipfsDemo", buildTypes = setOf(NativeBuildType.DEBUG))
    }
  }
}



