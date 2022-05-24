package danbroid.kipfs

import danbroid.kipfs.client.KNativeShell
import kotlinx.cinterop.*
import platform.posix.free


private object KIPFSLibNative : KIPFSNativeLib {


  val log = danbroid.logging.configure("TEST", coloured = true)

  override fun createNativeShell(address: String): Int =
    libkipfs.KCreateShell(address.cstr).useContents {
      r1?.convertToString()?.also {
        throw Exception(it)
      }
      r0
    }

  override fun disposeGoObject(ref: Int) = libkipfs.KDecRef(ref)

  override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray =
    libkipfs.KRequest(shellRefID, cmd.utf8, arg?.utf8).useContents {
      r2?.convertToString()?.also {
        throw Exception("Request failed: $it")
      }
      r0!!.readBytes(r1.toInt())
    }

  override fun getMessage(): String = libkipfs.KGetMessage()!!.copyToString()
  override fun getMessage2(): String = libkipfs.KGetMessage2()!!.copyToString()
  override fun dagCID(json: String): String = libkipfs.KCID(json.cstr)!!.copyToString()
  override fun createShell(ipfsAddress: String): Shell = KNativeShell(this, ipfsAddress)
  override fun environment(key: String): String? = platform.posix.getenv(key)?.toKString()
}

actual fun initKIPFSLib(): KIPFSLib {
  return KIPFSLibNative
}


fun CPointer<ByteVar>.copyToString(): String = this.toKString().also {
  free(this)
}


