@file:Suppress("UNUSED_PARAMETER")
@file:OptIn(ExperimentalForeignApi::class)

package kipfs.golib.jni

import kipfs.golib.copyToKString
import kotlinx.cinterop.*
import klog.klog
import platform.android.*
import org.danbrough.kipfs.*
import kotlin.experimental.ExperimentalNativeApi

private object JNIImpl

val log = JNIImpl.klog()

@OptIn(ExperimentalNativeApi::class)
internal fun jniInit() {
  Platform.isMemoryLeakCheckerActive = true
}

@CName("Java_kipfs_golib_KIPFSJni_dagCID")
fun dagCID(env: CPointer<JNIEnvVar>, thiz: jclass, json: jstring): jstring {
  memScoped {
    jniInit()
    val jsonC = env.pointed.pointed!!.GetStringUTFChars!!(env, json, null)
    val s = libkipfs.KCID(jsonC)!!
    env.pointed.pointed!!.ReleaseStringUTFChars!!(env, json, jsonC)
    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!
  }
}


@CName("Java_kipfs_golib_KIPFSJni_getTime")
fun getTime(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    jniInit()
    return libkipfs.GetTime().let { cs ->
      env.pointed.pointed!!.NewStringUTF!!.invoke(env, cs!!.getPointer(this))!!
    }
  }
}


@CName("Java_kipfs_golib_KIPFSJni_disposeGoObject")
fun disposeGoObject(env: CPointer<JNIEnvVar>, thiz: jclass, refnum: jint) {
  libkipfs.KDecRef(refnum)
}


@CName("Java_kipfs_golib_KIPFSJni_createNativeShell")
fun createNativeShell(env: CPointer<JNIEnvVar>, thiz: jclass, address: jstring): jint {
  memScoped {
    jniInit()
    val e = env.pointed.pointed!!
    val addrC = e.GetStringUTFChars!!(env, address, null)
    val s = libkipfs.KCreateShell(addrC).getPointer(this).pointed
    e.ReleaseStringUTFChars!!(env, address, addrC)
    if (s.r1 != null) {
      log.error("An error occurred")
      return -1
    }
    return s.r0
  }
}

@CName("Java_kipfs_golib_KIPFSJni_request")
fun request(
  env: CPointer<JNIEnvVar>,
  thiz: jclass,
  shellRefID: jint,
  cmd: jstring,
  arg: jstring?
): jbyteArray? {
  memScoped {
    jniInit()

    log.debug("Java_kipfs_golib_KIPFSJni_request()")
    val e = env.pointed.pointed!!
    val cmdC = e.GetStringUTFChars!!(env, cmd, null)

    val argC = arg?.let {
      e.GetStringUTFChars!!(env, arg, null)
    }

    libkipfs.KRequest(shellRefID, cmdC, argC).useContents {
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