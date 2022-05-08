object KIPFSLibJNI : KIPFSLib {
  private val log = danbroid.logging.getLog("TEST")

  init {
    log.error("INIT KIPFSLibJNI")
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
  external fun createShellJNI(address: String): Int
  external fun disposeGoObject(ref:Int)

  override fun createShell(url: String): KShell {
    return object : KShell {
      override fun id(): String = "FAke_ID"
    }
  }


}






