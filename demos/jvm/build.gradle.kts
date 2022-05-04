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
  implementation("com.github.danbrough.kipfs:jniLinuxAmd64:_")
  implementation(AndroidUtils.logging)
}