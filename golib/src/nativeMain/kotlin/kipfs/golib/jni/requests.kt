@file:Suppress("UNUSED_PARAMETER")

package kipfs.golib.jni

import kipfs.golib.copyToKString
import kotlinx.cinterop.*
import platform.android.*
import org.danbrough.kipfs.*
import org.danbrough.kipfs.golib.KRequestSend_return


/*JNIEXPORT jbyteArray JNICALL Java_kipfs_golib_jni_JNI_sendRequest
  (JNIEnv *, jclass, jint);*/
@CName("Java_kipfs_golib_KIPFSJni_sendRequest")
fun sendRequest(env: CPointer<JNIEnvVar>, thiz: jclass, requestRefID: jint): jbyteArray =
  memScoped {
    jniInit()
    val e = env.pointed.pointed!!

    org.danbrough.kipfs.golib.KRequestSend(requestRefID).useContents<KRequestSend_return, jbyteArray> {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      return r0!!.readBytes(r1.toInt()).let { bytes ->
        //  log.warn("RESULT: ${bytes.decodeToString()}")
        val jbytes = e.NewByteArray!!(env, r1.toInt())!!

        bytes.usePinned {
          e.SetByteArrayRegion!!(env, jbytes, 0, r1.toInt(), it.addressOf(0))
        }

        jbytes
      }
    }
  }

/*
JNIEXPORT void JNICALL Java_kipfs_golib_jni_JNI_requestOption
  (JNIEnv *, jclass, jint, jstring, jstring);
*/
@CName("Java_kipfs_golib_KIPFSJni_requestOption")
fun requestOption(
  env: CPointer<JNIEnvVar>,
  thiz: jclass,
  requestRefID: jint,
  jName: jstring,
  jValue: jstring
) = memScoped {
  jniInit()
  val e = env.pointed.pointed!!
  val cName = e.GetStringUTFChars!!(env, jName, null)
  val cValue = e.GetStringUTFChars!!(env, jValue, null)
  org.danbrough.kipfs.golib.KRequestOption(requestRefID, cName, cValue)
  e.ReleaseStringUTFChars!!(env, jName, cName)
  e.ReleaseStringUTFChars!!(env, jValue, cValue)
}

/*JNIEXPORT jbyteArray JNICALL Java_kipfs_golib_jni_JNI_postData
  (JNIEnv *, jclass, jint, jbyteArray);*/
@CName("Java_kipfs_golib_KIPFSJni_postData")
fun postData(
  env: CPointer<JNIEnvVar>,
  thiz: jclass,
  requestRefID: jint,
  jData: jbyteArray
): jbyteArray = memScoped {
  jniInit()
  val e = env.pointed.pointed!!
  val cData = e.GetByteArrayElements!!(env, jData, null)
  val dataLength = e.GetArrayLength!!(env, jData)
  return org.danbrough.kipfs.golib.KRequestPostBytes(requestRefID, cData, dataLength).useContents {
    r2?.copyToKString()?.also {
      throw Exception("PostData failed: $it")
    }
    r0!!.readBytes(r1.toInt()).let { bytes ->
      log.warn("RESULT: ${bytes.decodeToString()}")
      val jbytes = e.NewByteArray!!(env, r1.toInt())!!
      
      bytes.usePinned {
        e.SetByteArrayRegion!!(env, jbytes, 0, r1.toInt(), it.addressOf(0))
      }
      
      jbytes
    }
  }
}


/*JNIEXPORT jint JNICALL Java_kipfs_golib_jni_JNI_createRequest
  (JNIEnv *, jclass, jint, jstring, jstring)*/
@CName("Java_kipfs_golib_KIPFSJni_createRequest")
fun createRequest(
  env: CPointer<JNIEnvVar>,
  thiz: jclass,
  shellRefId: jint,
  jCommand: jstring,
  jArg: jstring?
): Int =
  memScoped {
    jniInit()
    val e = env.pointed.pointed!!
    val cCommand = e.GetStringUTFChars!!(env, jCommand, null)
    val cArg = if (jArg != null) e.GetStringUTFChars!!(env, jArg, null) else null
    return org.danbrough.kipfs.golib.KCreateRequest(shellRefId, cCommand, cArg).useContents {
      e.ReleaseStringUTFChars!!(env, jCommand, cCommand)
      if (jArg != null) e.ReleaseStringUTFChars!!(env, jArg, cArg)
      r1?.copyToKString()?.also {
        throw Exception("CreateRequest failed: $it")
      }
      r0
    }
  }
