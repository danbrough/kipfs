@file:Suppress("RemoveRedundantCallsOfConversionMethods")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalForeignApi::class)

package kipfs.golib

import kipfs.KByteResponse
import kipfs.KIPFS
import kipfs.KResponse
import kipfs.KShell
import kipfs.golib.KIPFSNative
import kotlinx.cinterop.*
import platform.posix.free
import org.danbrough.kipfs.*


/*
  external override fun createNativeShell(address: String): Int
  external override fun disposeGoObject(ref: Int)
  external override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray

  override fun createShell(ipfsAddress: String): Shell = KNativeShell(this, ipfsAddress)

  override fun environment(key: String): String? = System.getenv(key)
 */



fun CPointer<ByteVar>.copyToKString(): String = toKString().let {
  free(this)
  it
}


@OptIn(UnsafeNumber::class)
actual fun initKIPFSLib(): KIPFS = object : KIPFSNative {

  override fun createNativeShell(address: String): Int =
    libkipfs.KCreateShell(address.cstr).useContents {
      r1?.copyToKString()?.also {
        throw Exception(it)
      }
      r0
    }
  
  override fun createRequest(shellRef: Int,command:String,arg:String?): Int =
    libkipfs.KCreateRequest(shellRef,command.utf8,arg?.utf8).useContents {
      r1?.copyToKString()?.also {
        throw Exception("Create request failed: $it")
      }
      r0
    }


  override fun requestOption(requestRefID: Int, name: String, value: String) =

    libkipfs.KRequestOption(requestRefID,name.utf8,value.utf8)


  override fun sendRequest(requestRefID: Int): ByteArray =
    libkipfs.KRequestSend(requestRefID).useContents {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      r0!!.readBytes(r1.toInt())
    }

  override fun <T> postString(shellRefID: Int, data: String): KResponse<T> =
   libkipfs.KRequestPostString(shellRefID,data.utf8).useContents {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      KByteResponse<T>(r0!!.readBytes(r1.toInt()))
    }


  override fun <T> postData(shellRefID: Int, data: ByteArray): KResponse<T> =
   libkipfs.KRequestPostBytes(shellRefID,data.toCValues(),data.size).useContents {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      KByteResponse<T>(r0!!.readBytes(r1.toInt()))
    }

  override fun disposeGoObject(ref: Int) =libkipfs.KDecRef(ref)

/*  override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray =
   libkipfs.KRequest(shellRefID, cmd.utf8, arg?.utf8).useContents {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      r0!!.readBytes(r1.toInt())
    }
  */

  override fun createShell(ipfsAddress: String): KShell = KNativeShell(this, ipfsAddress)

  override fun environment(key: String): String? = platform.posix.getenv(key)?.toKString()

  override fun getTime(): String =libkipfs.GetTime()!!.copyToKString()

  override fun dagCID(json: String) =libkipfs.KCID(json.cstr)!!.copyToKString()
  
}


