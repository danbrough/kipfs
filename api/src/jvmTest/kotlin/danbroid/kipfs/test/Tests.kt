package danbroid.kipfs.test


import kotlin.test.Test

val log = danbroid.logging.configure("TEST", coloured = true)

class Tests {
  @Test
  fun test1() {
    log.info("test1()")
  }
}