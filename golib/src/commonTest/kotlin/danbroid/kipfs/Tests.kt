package danbroid.kipfs

import danbroid.kipfs.initKipfsLib
import kotlin.test.Test

class Tests {


  private val log = log()

  @Test
  fun getTime() {
    val kipfsLib = initKipfsLib()

    log.warn("running getTime test...")
    log.info("kipfsLib.getTime() = ${kipfsLib.getTime()}")
  }
}
