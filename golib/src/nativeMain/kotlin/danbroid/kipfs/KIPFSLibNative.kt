package danbroid.kipfs

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

  private val log = danbroid.logging.configure("KIPFS", coloured = true)
  override fun createNativeShell(address: String): Int = kipfs.KCreateShell(address.cstr).useContents {
    r1?.copyToKString()?.also {
      throw Exception(it)
    }
    r0
  }


  override fun disposeGoObject(ref: Int) = kipfs.KDecRef(ref)

  override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray =
    kipfs.KRequest(shellRefID, cmd.utf8, arg?.utf8).useContents {
      r2?.copyToKString()?.also {
        throw Exception("Request failed: $it")
      }
      r0!!.readBytes(r1.toInt())
    }

  override fun createShell(ipfsAddress: String): KShell = KNativeShell(this, ipfsAddress)

  override fun environment(key: String): String? = platform.posix.getenv(key)?.toKString()


  override fun getTime(): String = kipfs.GetTime()!!.copyToKString()

  override fun dagCID(json: String) = kipfs.KCID(json.cstr)!!.copyToKString()


}


