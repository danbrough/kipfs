package danbroid.kipfs

import danbroid.logging.DBLog
import kotlinx.cinterop.toKString
import platform.posix.free

object KipfsLibNative : KipfsLib {

  val log = danbroid.logging.configure("KIPFS", coloured = true)

  override fun getTime(): String = kipfs.GetTime()!!.let {
    val s = it.toKString()
    free(it)
    s
  }
}

actual fun initKipfsLib(): KipfsLib = KipfsLibNative


actual fun Any.log(): DBLog = KipfsLibNative.log.let {
  danbroid.logging.getLog(this::class)
}
