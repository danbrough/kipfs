import kotlin.test.Test

class Tests {
  companion object {
    val log = danbroid.logging.configure("TEST", coloured = true)
  }

  @Test
  fun test1() {

    log.info(getMessage())
    log.info(getMessage2())
    log.info(getMessage3())
    log.debug("dagCid: ${dagCID("\"Hello World\"")}")

  }
}