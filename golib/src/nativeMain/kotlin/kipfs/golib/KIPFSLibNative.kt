package kipfs.golib

import kipfs.KIPFS
import kipfs.KShell
import kotlinx.cinterop.*
import platform.posix.free


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


actual fun initKIPFSLib(): KIPFS = object : KIPFSNativeLib {

  override fun createNativeShell(address: String): Int =
    kipfsgo.KCreateShell(address.cstr).useContents {
      r1?.copyToKString()?.also {
        throw Exception(it)
      }
      r0
    }


  override fun disposeGoObject(ref: Int) = kipfsgo.KDecRef(ref)

  override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray =
    kipfsgo.KRequest(shellRefID, cmd.utf8, arg?.utf8).useContents {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      r0!!.readBytes(r1.toInt())
    }

  override fun createShell(ipfsAddress: String): KShell = KNativeShell(this, ipfsAddress)

  override fun environment(key: String): String? = platform.posix.getenv(key)?.toKString()


  override fun getTime(): String = kipfsgo.GetTime()!!.copyToKString()

  override fun dagCID(json: String) = kipfsgo.KCID(json.cstr)!!.copyToKString()
  
}


