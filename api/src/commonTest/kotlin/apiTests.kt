import danbroid.kipfs.ResponseID
import danbroid.kipfs.decodeJson
import kotlin.test.Test

class ApiTests {

  companion object {
    private val log = danbroid.logging.configure("TEST", coloured = true)
    val kipfs = initKIPFSLib()
  }

  @Test
  fun test() {
    log.warn("running test()")
    val shell = kipfs.createShell("/ip4/192.168.1.4/tcp/5001")
    shell.id().also {
      log.info("got response: $it")
    }
  }
}