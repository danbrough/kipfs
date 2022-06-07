package danbroid.godemo

import kotlinx.cinterop.toKString
import platform.posix.free

object GoLibNative : GoLib {
  override fun getTime(): String = godemo.GetTime()!!.let {
    val s = it.toKString()
    free(it)
    s
  }
}

actual fun initGoLib(): GoLib = GoLibNative


