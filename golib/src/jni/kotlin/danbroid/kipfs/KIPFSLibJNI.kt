package danbroid.kipfs

import danbroid.kipfs.client.KNativeShell


object KIPFSLibJNI : KIPFSNativeLib {
  private val log= danbroid.logging.configure("TEST", coloured = true)

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
  external override fun createNativeShell(address: String): Int
  external override fun disposeGoObject(ref: Int)
  external override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray

  override fun createShell(url: String): Shell = KNativeShell(this, url)
  override fun environment(key: String): String? = System.getenv(key)

}






