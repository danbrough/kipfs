import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

object KIFPS {
  const val group = "org.danbrough.kipfs"
}

fun KotlinMultiplatformExtension.declareTargets(){
  linuxX64()
}