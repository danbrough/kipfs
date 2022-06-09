package danbroid.kipfs

import danbroid.logging.DBLog
import kotlinx.cinterop.*
import platform.posix.free

interface KIPFSNativeLib : KipfsLib {
  fun createNativeShell(address: String): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray

}

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

private object KipfsLibNative : KIPFSNativeLib {

  private val log = danbroid.logging.configure("KIPFS", coloured = true)
  override fun createNativeShell(address: String): Int = kipfs.KCreateShell(address.cstr).useContents {
    r1?.copyToKString()?.also {
      throw Exception(it)
    }
    r0
  }


  override fun disposeGoObject(ref: Int) = kipfs.KDecRef(ref)

  override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray {
    TODO("Not yet implemented")
  }

  override fun getTime(): String = kipfs.GetTime()!!.copyToKString()

  override fun dagCID(json: String) = kipfs.KCID(json.cstr)!!.copyToKString()
}

actual fun initKipfsLib(): KipfsLib = KipfsLibNative


actual fun Any.log(): DBLog = KipfsLibNative.let {
  danbroid.logging.getLog(this::class)
}
