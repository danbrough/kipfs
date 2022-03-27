object KIPFSJNI  : KIPFS {
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
}


actual fun initLib(): KIPFS = KIPFSJNI

