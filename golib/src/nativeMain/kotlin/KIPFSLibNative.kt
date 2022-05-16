import kotlinx.cinterop.*
import platform.posix.free


object KIPFSLibNative : KIPFSLib {

  val log = danbroid.logging.getLog(KIPFSLibNative::class)

  override fun getMessage(): String = libkipfs.KGetMessage()!!.copyToString()
  override fun getMessage2(): String = libkipfs.KGetMessage2()!!.copyToString()
  override fun dagCID(json: String): String = libkipfs.KCID(json.cstr)!!.copyToString()

  override fun createShell(url: String): KShell {
    return object : KShell {
      override fun connect() {
        log.warn("connect() NOT IMPLEMENTED")
      }

      override fun dispose() {
        log.warn("dispose() NOT IMPLEMENTED")
      }

      override fun id(): String = "fake_id"

      override fun request(command: String, arg: String?): ByteArray {
        TODO()
      }
    }
  }

  override fun environment(key: String): String? = platform.posix.getenv(key)?.copyToString()
}

actual fun initKIPFSLib(): KIPFSLib = KIPFSLibNative


fun CPointer<ByteVar>.copyToString(): String = this.toKString().also {
  free(this)
}


