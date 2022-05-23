package danbroid.kipfs

import kotlinx.cinterop.*
import libkipfs.*
import platform.android.*

private fun init() {
  initRuntimeIfNeeded()
  Platform.isMemoryLeakCheckerActive = true
}


private val log = danbroid.logging.configure("KIPFS_JNI", coloured = true)

fun CPointer<ByteVar>.convertToString(): String = this.toKString().also {
  platform.posix.free(this)
}

@CName("Java_danbroid_kipfs_KIPFSLibJNI_getMessage")
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

@CName("Java_danbroid_kipfs_KIPFSLibJNI_getMessage2")
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

@CName("Java_danbroid_kipfs_KIPFSLibJNI_dagCID")
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

@CName("Java_danbroid_kipfs_KIPFSLibJNI_createNativeShell")
fun createNativeShell(env: CPointer<JNIEnvVar>, thiz: jclass, address: jstring): jint {
  memScoped {
    init()
    val e = env.pointed.pointed!!
    val addrC = e.GetStringUTFChars!!(env, address, null)
    val s = KCreateShell(addrC).getPointer(this).pointed
    e.ReleaseStringUTFChars!!(env, address, addrC)
    if (s.r1 != null) {
      log.error("An error occurred")
      return -1
    }
    return s.r0

    /*return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env,
      s.getPointer(this)
    )!!*/
  }
}


@CName("Java_danbroid_kipfs_KIPFSLibJNI_request")
fun request(
  env: CPointer<JNIEnvVar>,
  thiz: jclass,
  shellRefID: jint,
  cmd: jstring,
  arg: jstring?
): jbyteArray? {
  memScoped {
    init()

    log.debug("Java_danbroid_kipfs_KIPFSLibJNI_request()")
    val e = env.pointed.pointed!!
    val cmdC = e.GetStringUTFChars!!(env, cmd, null)

    val argC = arg?.let {
      e.GetStringUTFChars!!(env, arg, null)
    }

    KRequest(shellRefID, cmdC, argC).useContents {
      e.ReleaseStringUTFChars!!(env, cmd, cmdC)

      if (argC != null) e.ReleaseStringUTFChars!!(env, arg, argC)


      r2?.convertToString()?.also {
        init()
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


@CName("Java_danbroid_kipfs_KIPFSLibJNI_disposeGoObject")
fun disposeGoObject(env: CPointer<JNIEnvVar>, thiz: jclass, refnum: jint) {
  KDecRef(refnum)
}


