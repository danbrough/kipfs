import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

const val KIPFS_GROUP  = "org.danbrough.kipfs"
const val KIPFS_VERSION = "0.0.1-beta02"

fun KotlinMultiplatformExtension.declareTargets() {
  linuxX64()
  linuxArm64()
  linuxArm32Hfp()
  /*
  androidNativeX86()
  androidNativeX64()
  androidNativeArm32()
  androidNativeArm64()
  mingwX64()*/
}
