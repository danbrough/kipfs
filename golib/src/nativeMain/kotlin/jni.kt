import kotlinx.cinterop.*
import kotlinx.cinterop.cstr

import platform.posix.free

actual fun getMessage(): String = libkipfs.KGetMessage()!!.copyToString()
actual fun getMessage2(): String = libkipfs.KGetMessage2()!!.copyToString()
actual fun initLib() {}

actual fun dagCID(json: String): String = libkipfs.KCID(json.cstr)!!.copyToString()

@CName("Java_GoKIPFS_getMessage")
fun getMessage(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    init()
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      libkipfs.KGetMessage()!!.getPointer(this)
      //"The time is ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}!".cstr.ptr
    )!!
  }
}

@CName("Java_GoKIPFS_getMessage2")
fun getMessage2(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    init()
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      libkipfs.KGetMessage2()!!.getPointer(this)
      //"The time is ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}!".cstr.ptr
    )!!
  }
}

@CName("Java_GoKIPFS_dagCID")
fun dagCID(env: CPointer<JNIEnvVar>, thiz: jclass, json: jstring): jstring {
  memScoped {
    init()
    val jsonC = env.pointed.pointed!!.GetStringUTFChars!!(env, json, null)
    val s = libkipfs.KCID(jsonC)!!
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!
  }
}


private fun init() {
  initRuntimeIfNeeded()
  Platform.isMemoryLeakCheckerActive = true
}


fun CPointer<ByteVar>.copyToString(): String = this.toKString().also {
  free(this)
}


fun testNative() {
  println("Hello from native land")

}
