import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cstr
import kotlinx.cinterop.toKString
import platform.posix.free


object KIPFSLibNative : KIPFSLib {

  override fun getMessage(): String = libkipfs.KGetMessage()!!.copyToString()
  override fun getMessage2(): String = libkipfs.KGetMessage2()!!.copyToString()
  override fun dagCID(json: String): String = libkipfs.KCID(json.cstr)!!.copyToString()

  override fun createShell(url: String): KShell {
    libkipfs.KCreateShell(url.cstr)
    return object : KShell {

    }
  }
}

actual fun initKIPFSLib(): KIPFSLib = KIPFSLibNative





fun CPointer<ByteVar>.copyToString(): String = this.toKString().also {
  free(this)
}


