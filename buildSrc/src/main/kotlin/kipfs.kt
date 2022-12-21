import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

const val KIPFS_GROUP  = "org.danbrough.kipfs"
const val KIPFS_VERSION = "0.0.1-beta01"

fun KotlinMultiplatformExtension.declareTargets() {
  linuxX64()
}
