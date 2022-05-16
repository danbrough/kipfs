import kotlin.test.Test


class Tests {

  companion object {
    const val CID_HELLO_WORLD = "QmSXfamUcvkEyYbtGdQdwZ4TrnVbneKrY5zNx7eGV5ckyq"
    val log = danbroid.logging.getLog(Tests::class)


    val kipfs: KIPFSLib by lazy { initKIPFSLib() }

    val ipfsAddress by lazy {
      kipfs.environment(ENV_KIPFS_ADDRESS)?.also {
        log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
        return@lazy it
      }

      DEFAULT_KIPFS_ADDRESS.also {
        log.warn("using $ENV_KIPFS_ADDRESS not set. Using default address: $it")
      }
    }


  }


  @Test
  fun test() {
    log.info("test1() ..")

    log.debug(kipfs.getMessage())
    log.debug(kipfs.getMessage2())

    log.trace("DAG ${kipfs.dagCID("\"Hello World\"")}")

    val shell = kipfs.createShell("/ip4/192.168.1.4/tcp/5001")
    log.trace("created shell")
    shell.id().also {
      log.warn("ID: $it")
    }


  }


}