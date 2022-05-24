import danbroid.kipfs.DEFAULT_KIPFS_ADDRESS
import danbroid.kipfs.ENV_KIPFS_ADDRESS
import danbroid.kipfs.api.basic.id
import danbroid.kipfs.initKIPFSLib
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class ApiTests {

  companion object {
    private val log = danbroid.logging.configure("TEST", coloured = true)
    val kipfs = initKIPFSLib()

    val ipfsAddress: String by lazy {
      kipfs.environment(ENV_KIPFS_ADDRESS)?.also {
        log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
      } ?: DEFAULT_KIPFS_ADDRESS.also {
        log.warn("using $ENV_KIPFS_ADDRESS not set. Using default address: $it")
      }
    }

    val shell by lazy { kipfs.createShell(ipfsAddress) }

  }

  @Test
  fun test() {
    log.warn("running test()")

    runBlocking {

      shell.id().also {
        log.info("got response: $it")
      }
    }
  }
}