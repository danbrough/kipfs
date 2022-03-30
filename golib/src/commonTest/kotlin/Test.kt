import kotlin.test.Test

val log = danbroid.logging.configure("TEST", coloured = true)

class Test {

  companion object {
    val kipfs = initKIPFSLib()
  }


  @Test
  fun test() {
    log.info("test1() ..")

    log.debug(kipfs.getMessage())
    log.debug(kipfs.getMessage2())

    log.trace("DAG ${kipfs.dagCID("\"Hello World\"")}")

    val shell = kipfs.createShell("/ip4/192.168.1.4/tcp/5001")
    log.trace("created shell")
    shell.id().also {
      log.warn("ID: $it")
    }


  }


}