package danbroid.demo

import kotlin.test.Test

object KtorTest {
  val log = danbroid.logging.configure("TEST", coloured = true)

  @Test
  fun test1() {
    log.warn("running test1")
  }
}