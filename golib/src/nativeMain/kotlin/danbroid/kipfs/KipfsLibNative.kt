package danbroid.kipfs

import danbroid.logging.DBLog
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cstr
import kotlinx.cinterop.toKString
import platform.posix.free

interface KIPFSNativeLib : KipfsLib {
  fun createNativeShell(address: String): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray
}

fun CPointer<ByteVar>.copyToKString(): String = toKString().let {
  free(this)
  it
}

object KipfsLibNative : KipfsLib {

  private val log = danbroid.logging.configure("KIPFS", coloured = true)

  override fun getTime(): String = kipfs.GetTime()!!.copyToKString()

  override fun dagCID(json: String) = kipfs.KCID(json.cstr)!!.copyToKString()
}

actual fun initKipfsLib(): KipfsLib = KipfsLibNative


actual fun Any.log(): DBLog = KipfsLibNative.let {
  danbroid.logging.getLog(this::class)
}
