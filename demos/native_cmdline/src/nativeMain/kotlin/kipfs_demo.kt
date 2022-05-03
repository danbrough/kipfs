fun main(args: Array<String>) {

  val log = danbroid.logging.configure("TEST", coloured = true)
  val kipfs = initKIPFSLib()

  val ipfsAddress = if (args.size == 0) "/ip4/127.0.0.1/tcp/5001".also {
    log.warn("ipfs uri not provided. Using default: $it")
  } else args[0]

  log.warn("message: ${kipfs.getMessage()}")

  kipfs.dagCID("\"Hello World\"").also {
    log.debug("cid: $it")
  }

  log.debug("connecting to $ipfsAddress")

  val shell = kipfs.createShell(ipfsAddress)
  log.debug("created shell: $shell")

  log.trace("ID: ${shell.id()}")


}