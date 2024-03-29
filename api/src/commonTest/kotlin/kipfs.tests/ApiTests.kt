package kipfs.tests

import kipfs.DEFAULT_KIPFS_ADDRESS
import kipfs.ENV_KIPFS_ADDRESS
import kipfs.api.*
import kipfs.golib.initKIPFSLib
import kipfs.serialization.decodeJson
import klog.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiTests {
  
  companion object {
    
    private val log = klog {
      level = Level.TRACE
      messageFormatter = KMessageFormatters.verbose.colored
      writer = KLogWriters.stdOut
    }
    
    const val DAG_HELLO_WORLD = "bafyreidfq7gnjnpi7hllpwowrphojoy6hgdgrsgitbnbpty6f2yirqhkom"
    
    private val kipfs = initKIPFSLib()
    
    private val ipfsAddress: String by lazy {
      kipfs.environment(ENV_KIPFS_ADDRESS)?.also {
        log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
      } ?: DEFAULT_KIPFS_ADDRESS.also {
        log.warn("$ENV_KIPFS_ADDRESS not set. Using default address: $it")
      }
    }
    
    private val shell by lazy { kipfs.createShell(ipfsAddress) }
  }
  
  
  @Test
  fun testID() {
    log.warn("running test()")
    
    runBlocking {
      log.info("got response: ${shell.id().decodeJson()}")
    }
  }
  
  @Test
  fun testDAG() {
    log.debug("testDAG()")
    
    runBlocking {
      log.trace("DAG_HELLO_WORLD = ${shell.dagGet<String>(DAG_HELLO_WORLD).readAll().decodeToString()}")
    }
  }
  
  @Test
  fun testDagPut() {
    log.info("testDagPut()")
    
    runBlocking {
      shell.dagPut("\"Hello World\"".encodeToByteArray()).readAll().decodeJson<ResponseDagPut>().also {
        log.info("response: $it")
       assertEquals(DAG_HELLO_WORLD,it.cid.value,"DAG_HELLO_WORLD != ${it.cid.value}")
      }
    }
  }
  
  @Test
  fun testMultibase() {
    log.warn("running testMultibase()")
    
    runBlocking {
      shell.multibaseEncode("123").readAll().decodeToString().also {
        log.info("response: $it")
      }
    }
  }
}