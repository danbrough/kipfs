fun main() {


  val log = danbroid.logging.configure("TEST", coloured = true)

  initLib()

  log.warn("message: ${getMessage()}")

  dagCID("\"Hello World\"").also {
    log.debug("cid: $it")
  }

}