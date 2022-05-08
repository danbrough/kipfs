
import kotlin.test.Test


class JvmTests {

  @Test
  fun jvmTest(){
    log.info("jvmTest() my man")
    val refnum = KIPFSLibJNI.createShellJNI("/ip4/192.168.1.4/tcp/5001")

  }
}