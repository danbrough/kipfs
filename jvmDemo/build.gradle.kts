plugins {
  application
  kotlin("jvm")
}

application {
  mainClass.set("danbroid.jvmdemo.Demo1")
}


tasks.register("demo1", JavaExec::class) {
  mainClass.set("danbroid.jvmdemo.Demo1")
  classpath = sourceSets["main"].compileClasspath + sourceSets["main"].runtimeClasspath
  println("CLASSPATH ${classpath.files}")
}


dependencies {
  implementation("danbroid.mpp:jni:_")
  implementation("danbroid.mpp:nativeLinuxx64Debug:_")
  implementation(AndroidUtils.logging)
}