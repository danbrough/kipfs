import jni.JNIEnvVar
import jni.jclass
import jni.jint
import jni.jstring
import kotlinx.cinterop.*
import libkipfs.*

private fun init() {
  initRuntimeIfNeeded()
  Platform.isMemoryLeakCheckerActive = true
}


@CName("Java_KIPFSLibJNI_getMessage")
fun getMessage(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    init()

    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      KGetMessage()!!.getPointer(this)
      //"The time is ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}!".cstr.ptr
    )!!
  }
}

@CName("Java_KIPFSLibJNI_getMessage2")
fun getMessage2(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    init()
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      KGetMessage2()!!.getPointer(this)
      //"The time is ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}!".cstr.ptr
    )!!
  }
}

@CName("Java_KIPFSLibJNI_dagCID")
fun dagCID(env: CPointer<JNIEnvVar>, thiz: jclass, json: jstring): jstring {
  memScoped {
    init()
    val jsonC = env.pointed.pointed!!.GetStringUTFChars!!(env, json, null)
    val s = KCID(jsonC)!!
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!
  }
}

@CName("Java_KIPFSLibJNI_createShellJNI")
fun createShellJNI(env: CPointer<JNIEnvVar>, thiz: jclass, address: jstring): jint {
  memScoped {
    init()
    val e = env.pointed.pointed!!
    val addrC = e.GetStringUTFChars!!(env, address, null)
    println("got address")
    val s = KCreateShell(addrC).getPointer(this).pointed
    println("got s")
    e.ReleaseStringUTFChars!!(env,address,addrC)
    println("release string chars")
    if (s.r1 != null){
      println("An error occurred")
      return -1
    }
    return s.r0

    /*return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!*/
  }
}

@CName("Java_KIPFSLibJNI_disposeGoObject")
fun disposeGoObject(env: CPointer<JNIEnvVar>, thiz: jclass,refnum:jint) {
  KDestroyRef(refnum)
}
