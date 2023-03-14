import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

const val KIPFS_GROUP  = "org.danbrough.kipfs"
const val KIPFS_VERSION = "0.0.1-beta02"

fun KotlinMultiplatformExtension.declareTargets() {
  jvm()
  
  if (System.getProperty("idea.active").toBoolean()){
    when (HostManager.host){
      KonanTarget.LINUX_X64 -> linuxX64()
      KonanTarget.LINUX_ARM64 -> linuxArm64()
      KonanTarget.MACOS_ARM64 -> macosArm64()
      KonanTarget.MACOS_X64 -> macosX64()
      else -> {}
    }
  } else {



    if (HostManager.hostIsMac) {
      macosArm64()
      macosX64()
    } else {
      linuxX64()
      linuxArm64()
      linuxArm32Hfp()

      androidNativeX86()
      androidNativeX64()
      androidNativeArm32()
      androidNativeArm64()
      mingwX64()
    }
  }
}
