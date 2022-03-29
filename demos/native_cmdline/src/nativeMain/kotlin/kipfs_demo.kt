fun main() {

  val log = danbroid.logging.configure("TEST", coloured = true)
  val kipfs = initKIPFSLib()

  log.warn("message: ${kipfs.getMessage()}")



  kipfs.dagCID("\"Hello World\"").also {
    log.debug("cid: $it")
  }


  val shell = kipfs.createShell("/ip4/192.168.1.4/tcp/5001")
  log.debug("created shell: $shell")

  log.trace("ID: ${shell.id()}")



}