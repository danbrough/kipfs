import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  
}

kotlin {
  linuxX64()
  macosX64()
  jvm()
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(Dependencies.klog)
      }
    }
    
    
  }
  
  val commonTest by sourceSets.getting{
    dependencies {
      implementation(kotlin("test"))
    }
  }
  
  val nativeTest by sourceSets.creating {
    dependsOn(commonTest)
 
  }
  
  
  
  targets.withType(KotlinNativeTarget::class).all {
    compilations["test"].apply {
      defaultSourceSet.dependsOn(nativeTest)
    }
  }
}