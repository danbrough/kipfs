
object KIPFSLibJNI : KIPFSLib {
  val log = danbroid.logging.configure("TEST", coloured = true)

  init {
    runCatching {
      log.warn("loading gokipfs ..")
      System.loadLibrary("gokipfs")
    }
    log.warn("loading kipfs ..")
    System.loadLibrary("kipfs")
    log.warn("done")
  }


  external override fun getMessage(): String
  external override fun getMessage2(): String
  external override fun dagCID(json: String): String

  override fun createShell(url: String): KShell {
    TODO("Not yet implemented")
  }
}


actual fun initKIPFSLib(): KIPFSLib = KIPFSLibJNI




