import danbroid.kipfs.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


class Testing {

  companion object {
    const val CID_HELLO_WORLD = "QmSXfamUcvkEyYbtGdQdwZ4TrnVbneKrY5zNx7eGV5ckyq"

    val kipfs: KIPFSLib = initKIPFSLib()

    val log = danbroid.logging.getLog(Testing::class)

    val ipfsAddress: String by lazy {
      kipfs.environment(ENV_KIPFS_ADDRESS)?.also {
        log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
      } ?: DEFAULT_KIPFS_ADDRESS.also {
        log.warn("using $ENV_KIPFS_ADDRESS not set. Using default address: $it")
      }
    }
  }


  @Test
  fun test() {
    log.info("test1() ..")
    println("test()")

    log.debug(kipfs.getMessage())
    log.debug(kipfs.getMessage2())

    log.trace("DAG ${kipfs.dagCID("\"Hello World\"")}")
  }

  @Test
  fun cidAccess() {
    log.info("cidAccess()")
    runBlocking {
      val shell = kipfs.createShell(ipfsAddress)
      shell.request("cat", CID_HELLO_WORLD).also {
        log.debug("received: ${it.size} bytes: ${it.decodeToString()}")
      }
    }
  }

  private fun runTest(){


    runBlocking {
      val shell = kipfs.createShell(ipfsAddress)
      log.debug("created shell")

      val idRequest = suspend {
        shell.request("id").also {
          log.debug("received: size:${it.size} ${it.decodeToString()}")
        }
      }
      idRequest()
      idRequest()
      idRequest()
      idRequest()
      idRequest()
      idRequest()
    }
  }

  @Test
  fun cmdID() {
    log.info("cmdID()")
    log.debug("ipfsAddress: $ipfsAddress")
    runTest()
  }

}