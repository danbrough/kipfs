fun main() {

  val log = danbroid.logging.configure("TEST", coloured = true)
  val kipfs = initKIPFSLib()

  log.warn("message: ${kipfs.getMessage()}")



  kipfs.dagCID("\"Hello World\"").also {
    log.debug("cid: $it")
  }


}