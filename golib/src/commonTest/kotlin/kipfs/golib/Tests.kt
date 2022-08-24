package kipfs.golib

import kotlin.test.Test
import kotlin.test.assertEquals
import klog.*

class Tests {
  
  
  private val kipfsLib = initKIPFSLib()
  
  companion object {
    private val log = klog {
      level = Level.TRACE
      writer = KLogWriters.stdOut
      messageFormatter = KMessageFormatters.verbose.colored
    }
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
    
    assertEquals(cid, "bafyreidfq7gnjnpi7hllpwowrphojoy6hgdgrsgitbnbpty6f2yirqhkom")
    
    
  }
  

}
