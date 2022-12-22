package kipfs.golib

import kipfs.KByteResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import klog.*
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest

class Tests {
  
  
  private val kipfsLib = initKIPFSLib()
  
  companion object {
    const val DAG_HELLO_WORLD = "bafyreidfq7gnjnpi7hllpwowrphojoy6hgdgrsgitbnbpty6f2yirqhkom"
    private val log = klog("TESTS") {
      level = Level.TRACE
      writer = KLogWriters.stdOut
      messageFormatter = KMessageFormatters.verbose.colored
    }
  }
  
  private val shell by lazy {
    kipfsLib.createShell()
  }
  
  @BeforeTest
  fun beforeTest(){
    log.warn("beforeTest() $this")
  }
  
  
  @Test
  fun getTime() {
    log.warn("running getTime test...")
    log.info("kipfsLib.getTime() = ${kipfsLib.getTime()}")
  }
  
  @Test
  fun dagCID() {
    val json = """ "Hello World" """
    val cid = kipfsLib.dagCID(json)
    log.debug("json: $json => cid: $cid")
    
    assertEquals(cid, DAG_HELLO_WORLD)
  }
  
  @Test
  fun multibaseEncode(){
    runBlocking {
      shell.request<String>("dag/get", DAG_HELLO_WORLD).send().also {
        it as KByteResponse
        log.debug("result: ${it.data.decodeToString()}")
      }
    }
    
  }
  

}
