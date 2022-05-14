import danbroid.kipfs.client.KJNIShell

object KIPFSLibJNI : KIPFSLib {
  private val log = danbroid.logging.getLog("TEST")

  init {
    log.info("INIT KIPFSLibJNI")
    runCatching {
      log.debug("loading gokipfs ..")
      System.loadLibrary("gokipfs")
    }
    log.debug("loading kipfs ..")
    System.loadLibrary("kipfs")
    log.debug("finished loading native libraries")
  }


  external override fun getMessage(): String
  external override fun getMessage2(): String
  external override fun dagCID(json: String): String
  external fun createShellJNI(address: String): Int
  external fun disposeGoObject(ref: Int)
  external fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray?

  override fun createShell(url: String): KShell = KJNIShell(url)


}






