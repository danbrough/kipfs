package danbroid.kipfs

import kotlinx.cinterop.toKString
import platform.posix.free

object KipfsLibNative : KipfsLib {
  override fun getTime(): String = kipfs.GetTime()!!.let {
    val s = it.toKString()
    free(it)
    s
  }
}

actual fun initKipfsLib(): KipfsLib = KipfsLibNative



