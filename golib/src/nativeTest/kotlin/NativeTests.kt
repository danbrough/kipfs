import kotlin.test.Test

class NativeTests {


  @Test
  fun test1() {
    log.info("test1() running here")
    libkipfs.KGetMessage()!!.copyToString().also {
      log.debug("The message is $it")
    }
  }
}
