plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME


kotlin {

  android()

  linuxX64("linuxAmd64") {
  }


  jvm()

  sourceSets {

    commonMain {
      dependencies {
        implementation(AndroidUtils.logging)
        implementation(kotlin("stdlib"))
        implementation("danbroid.mpp:golib:_")
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))

      }
    }


    val linuxAmd64Main by getting {
      dependencies {
        //  implementation("org.jetbrains.kotlinx:atomicfu:_")
      }

      kotlin.srcDirs("src/nativeMain/kotlin")
    }

    val linuxAmd64Test by getting {
      dependencies {

      }
    }


    val jvmTest by getting {

    }


/*
    val nativeMain by getting {
      //kotlin.srcDir("src/nativeMain/kotlin")
      dependencies {
        implementation(AndroidUtils.logging)
        implementation(project(":golib"))
      }
    }
    val android386Main by getting {
      //kotlin.srcDir("src/nativeMain/kotlin")
      dependencies {
        implementation(AndroidUtils.logging)
        implementation(project(":golib"))
      }
    }
    val nativeTest by getting {
    }


    val klibDemoMain by getting {

    }

    val klibDemoTest by getting {
      dependencies {
        implementation(project.files("libs/golib2.klib").also {
          println("GOLIB FILES ${it.files}")
        })
      }

    }


    val jvmMain by getting {
      dependencies {
        implementation(AndroidUtils.logging)
        implementation("danbroid.mpp:golib:_")
        implementation("danbroid.mpp:golibDebugNative:_")
      }
    }
    val jvmTest by getting
*/


  }
}


android {
  compileSdk = ProjectVersions.SDK_VERSION
  sourceSets["main"].manifest.srcFile("src/AndroidManifest.xml")

  defaultConfig {
    minSdk = ProjectVersions.MIN_SDK_VERSION
    targetSdk = ProjectVersions.SDK_VERSION
  }
}
