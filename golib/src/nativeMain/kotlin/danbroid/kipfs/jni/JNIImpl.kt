package danbroid.kipfs

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import platform.android.JNIEnvVar
import platform.android.jclass
import platform.android.jint
import platform.android.jstring
import platform.linux.free

private fun init() {
  initRuntimeIfNeeded()
  Platform.isMemoryLeakCheckerActive = true
}


@CName("Java_danbroid_kipfs_JNI_getTime")
fun getTime(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    init()

    return kipfs.GetTime().let { cs ->
      env.pointed.pointed!!.NewStringUTF!!.invoke(env, cs!!.getPointer(this))!!
    }

  }
}


@CName("Java_danbroid_kipfs_JNI_disposeGoObject")
fun disposeGoObject(env: CPointer<JNIEnvVar>, thiz: jclass, refnum: jint) {
  kipfs.KDecRef(refnum)
}


@CName("Java_danbroid_kipfs_JNI_dagCID")
fun dagCID(env: CPointer<JNIEnvVar>, thiz: jclass, json: jstring): jstring {
  memScoped {
    init()
    val jsonC = env.pointed.pointed!!.GetStringUTFChars!!(env, json, null)
    val s = kipfs.KCID(jsonC)!!
    env.pointed.pointed!!.ReleaseStringUTFChars!!(env,json,jsonC)
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!
  }
}

