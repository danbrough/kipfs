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


  targets.withType(KotlinNativeTarget::class).all {
    compilations["main"].apply {
      //defaultSourceSet.dependsOn(nativeMain)
      defaultSourceSet {
        if (ProjectVersions.IDE_MODE) {
          kotlin.srcDir("src/nativeMain/kotlin")
          dependencies {
            implementation(project(":golib"))
          }
        } else {
          sourceSets.maybeCreate("nativeMain").apply {
            dependencies {
              implementation(KotlinX.coroutines.core)
            }
          }

          dependsOn(sourceSets.getByName("nativeMain"))

          dependencies {
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



