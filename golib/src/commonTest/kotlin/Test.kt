import kotlin.test.Test

class Test {

  companion object {
    val log = danbroid.logging.configure("TEST", coloured = true)
    val kipfs = initLib()
  }


  @Test
  fun test() {
    log.info("test1()!")


    log.debug(kipfs.getMessage())
    log.debug(kipfs.getMessage2())
    log.trace("DAG ${kipfs.dagCID("\"Hello World\"")}")
/*    log.debug("message is: ${KIgetMessage()}")
    log.warn("message2 is: ${getMessage2()}")
    log.trace("DAG ${dagCID("\"Hello World\"")}")*/
  }


}