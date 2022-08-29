@file:Suppress("UNUSED_PARAMETER")

package kipfs.golib.jni

import kipfs.golib.copyToKString
import kotlinx.cinterop.*
import klog.klog
import platform.android.*


private object JNIImpl

 val log = JNIImpl.klog()

internal fun jniInit() {
  initRuntimeIfNeeded()
  Platform.isMemoryLeakCheckerActive = true
}

@CName("Java_kipfs_golib_jni_JNI_getTime")
fun getTime(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    jniInit()
    
    return kipfsgo.GetTime().let { cs ->
      env.pointed.pointed!!.NewStringUTF!!.invoke(env, cs!!.getPointer(this))!!
    }
  }
}


@Suppress("SpellCheckingInspection")
@CName("Java_kipfs_golib_jni_JNI_disposeGoObject")
fun disposeGoObject(env: CPointer<JNIEnvVar>, thiz: jclass, refnum: jint) {
  kipfsgo.KDecRef(refnum)
}


@CName("Java_kipfs_golib_jni_JNI_dagCID")
fun dagCID(env: CPointer<JNIEnvVar>, thiz: jclass, json: jstring): jstring {
  memScoped {
    jniInit()
    val jsonC = env.pointed.pointed!!.GetStringUTFChars!!(env, json, null)
    val s = kipfsgo.KCID(jsonC)!!
    env.pointed.pointed!!.ReleaseStringUTFChars!!(env, json, jsonC)
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!
  }
}





@CName("Java_kipfs_golib_jni_JNI_createNativeShell")
fun createNativeShell(env: CPointer<JNIEnvVar>, thiz: jclass, address: jstring): jint {
  memScoped {
    jniInit()
    val e = env.pointed.pointed!!
    val addrC = e.GetStringUTFChars!!(env, address, null)
    val s = kipfsgo.KCreateShell(addrC).getPointer(this).pointed
    e.ReleaseStringUTFChars!!(env, address, addrC)
    if (s.r1 != null) {
      log.error("An error occurred")
      return -1
    }
    return s.r0
  }
}

@CName("Java_kipfs_golib_jni_JNI_request")
fun request(
  env: CPointer<JNIEnvVar>,
  thiz: jclass,
  shellRefID: jint,
  cmd: jstring,
  arg: jstring?
): jbyteArray? {
  memScoped {
    jniInit()
    
    log.debug("Java_kipfs_golib_jni_JNI_request()")
    val e = env.pointed.pointed!!
    val cmdC = e.GetStringUTFChars!!(env, cmd, null)
    
    val argC = arg?.let {
      e.GetStringUTFChars!!(env, arg, null)
    }
    
    kipfsgo.KRequest(shellRefID, cmdC, argC).useContents {
      e.ReleaseStringUTFChars!!(env, cmd, cmdC)
      
      if (argC != null) e.ReleaseStringUTFChars!!(env, arg, argC)
      
      r2?.copyToKString()?.also {
        val err = Exception("Request failed: $it")
        log.error(err.message, err)
        throw err
      }
      
      r0!!.readBytes(r1.toInt()).also { bytes ->
        //  log.warn("RESULT: ${bytes.decodeToString()}")
        val jbytes = e.NewByteArray!!(env, r1.toInt())!!
        
        bytes.usePinned {
          e.SetByteArrayRegion!!(env, jbytes, 0, r1.toInt(), it.addressOf(0))
        }
        
        return jbytes
      }
    }
    return null;
  }
}