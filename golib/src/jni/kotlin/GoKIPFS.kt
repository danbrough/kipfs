object GoKIPFS {
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


  fun touch() {}

  external fun getMessage(): String
  external fun getMessage2(): String
  external fun dagCID(json: String): String
}


actual fun initLib() = GoKIPFS.touch()

actual fun getMessage(): String = GoKIPFS.getMessage()
actual fun getMessage2(): String = GoKIPFS.getMessage2()


actual fun dagCID(json: String): String = GoKIPFS.dagCID(json)
