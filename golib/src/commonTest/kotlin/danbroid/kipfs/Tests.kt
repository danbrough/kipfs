package danbroid.kipfs

import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {


  private val log = log()
  private val kipfsLib = initKIPFSLib()

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

  @Test
  fun base32(){
    val text = "testing"
    
  }
}
