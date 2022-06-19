package danbroid.kipfs

import io.matthewnelson.component.encoding.base32.encodeBase32
import kotlin.test.Test
import kotlin.test.assertEquals
import org.danbrough.klog.*

class Tests {


  private val log = klog()
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
  fun base32() {
    val text = "testing"
    text.encodeToByteArray().encodeBase32().also {
      log.warn("$text -> $it")
    }
  }
}
