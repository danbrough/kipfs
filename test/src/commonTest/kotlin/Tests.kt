import org.danbrough.klog.Level
import org.danbrough.klog.klog
import kotlin.test.Test

val testLog = klog("STUFF").also {
  it.level = Level.TRACE
}

class Tests {


  @Test
  fun test() {

    testLog.trace("trace")
    testLog.debug("debug")
    testLog.info("info")
    testLog.warn("warn")
    testLog.error("error")
  }


  @Test
  fun test2() {
    val log = klog("STUFF.a")
    log.trace("trace for STUFF.a")
  }


}