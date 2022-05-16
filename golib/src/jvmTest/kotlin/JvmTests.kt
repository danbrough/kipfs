import danbroid.kipfs.KIPFSLibJNI
import kotlin.test.Test


class JvmTests {

  companion object{
    val log = danbroid.logging.getLog(JvmTests::class)
  }

  @Test
  fun jvmTest() {
    log.info("jvmTest()")

    log.debug("connecting to ${Testing.ipfsAddress}")
    val refnum = KIPFSLibJNI.createShellJNI(Testing.ipfsAddress)

    KIPFSLibJNI.disposeGoObject(refnum)
  }

  @Test
  fun requestID() {
    log.info("requestID()")
    val refnum = KIPFSLibJNI.createShellJNI(Testing.ipfsAddress)
    KIPFSLibJNI.request(refnum, "id").also {
      log.info("received byte array of length: ${it.size}")
      val data = it.decodeToString()
      log.warn("response: $data")


    }
    KIPFSLibJNI.disposeGoObject(refnum)
  }


}