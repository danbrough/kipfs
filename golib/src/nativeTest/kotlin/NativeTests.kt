import kotlinx.cinterop.cstr
import kotlinx.cinterop.useContents
import kotlin.test.Test

class NativeTests {


  @Test
  fun test1() {
    log.info("test1() running here")
    libkipfs.KGetMessage()!!.copyToString().also {
      log.debug("The message is $it")
    }

    libkipfs.KCreateShell("/ip4/192.168.1.4/tcp/5001".cstr)

    libkipfs.KCmdID2().also {
      log.warn("KCmdID2() returned $it")
      it.useContents {
        r1?.copyToString()?.also { err->
          throw Exception(err)
        } ?: r0!!.copyToString()
      }
    }
  }
}
