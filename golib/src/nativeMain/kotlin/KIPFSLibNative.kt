import kotlinx.cinterop.*
import platform.posix.free


object KIPFSLibNative : KIPFSLib {

  override fun getMessage(): String = libkipfs.KGetMessage()!!.copyToString()
  override fun getMessage2(): String = libkipfs.KGetMessage2()!!.copyToString()
  override fun dagCID(json: String): String = libkipfs.KCID(json.cstr)!!.copyToString()

  override fun createShell(url: String): KShell {
    libkipfs.KCreateShell(url.cstr)
    return object : KShell {

      override fun id(): String = idRequest()

    }
  }
}

actual fun initKIPFSLib(): KIPFSLib = KIPFSLibNative


private fun KShell.idRequest(): String = libkipfs.KCmdID2().useContents {
  r1?.let {
    it.copyToString().also { err ->

      throw Exception(err)
    }
  } ?: return r0!!.copyToString()
}


fun CPointer<ByteVar>.copyToString(): String = this.toKString().also {
  free(this)
}


