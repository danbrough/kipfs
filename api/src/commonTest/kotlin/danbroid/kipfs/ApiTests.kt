package danbroid.kipfs

import danbroid.kipfs.api.dagGet
import danbroid.kipfs.api.id
import io.matthewnelson.component.base64.Base64
import io.matthewnelson.component.base64.encodeBase64
import io.matthewnelson.component.base64.encodeBase64ToByteArray
import io.matthewnelson.component.encoding.base32.encodeBase32

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class ApiTests {

  companion object {
    const val DAG_HELLO_WORLD = "bafyreidfq7gnjnpi7hllpwowrphojoy6hgdgrsgitbnbpty6f2yirqhkom"

    private val log = log()
    private val kipfs = initKIPFSLib()

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


  @Test
  fun base64(){
    val text = "testing"
    text.encodeToByteArray().encodeBase64(Base64.UrlSafe(true)).also {
      log.warn("$text -> $it")
    }

  }
}