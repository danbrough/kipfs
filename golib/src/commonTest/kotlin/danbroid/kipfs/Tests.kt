package danbroid.kipfs

import danbroid.kipfs.initKipfsLib
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {


  private val log = log()
  private val kipfsLib = initKipfsLib()

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
