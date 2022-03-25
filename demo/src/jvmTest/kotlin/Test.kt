import kotlin.test.Test

class Test {
  companion object {
    val log = danbroid.logging.configure("TEST", coloured = true)
  }

  @Test
  fun test1() {
    log.info("test1()")
  }
}