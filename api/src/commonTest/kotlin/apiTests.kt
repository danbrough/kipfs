import danbroid.kipfs.DEFAULT_KIPFS_ADDRESS
import danbroid.kipfs.ENV_KIPFS_ADDRESS
import danbroid.kipfs.api.dagGet
import danbroid.kipfs.api.id

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class ApiTests {

  companion object {
    const val DAG_HELLO_WORLD = "bafyreidfq7gnjnpi7hllpwowrphojoy6hgdgrsgitbnbpty6f2yirqhkom"

    private val log = danbroid.logging.configure("TEST", coloured = true)
    private val kipfs = danbroid.kipfs.initKipfsLib()

    private val ipfsAddress: String by lazy {
      kipfs.environment(ENV_KIPFS_ADDRESS)?.also {
        log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
      } ?: DEFAULT_KIPFS_ADDRESS.also {
        log.warn("using $ENV_KIPFS_ADDRESS not set. Using default address: $it")
      }
    }

    private val shell by lazy { kipfs.createShell(ipfsAddress) }
  }


  @Test
  fun testID() {
    log.warn("running test()")

    runBlocking {
      log.info("got response: ${shell.id()}")
    }
  }

  @Test
  fun testDAG() {
    log.debug("testDAG()")

    runBlocking {
      log.trace("DAG_HELLO_WORLD = ${shell.dagGet<String>(DAG_HELLO_WORLD)}")
    }
  }
}