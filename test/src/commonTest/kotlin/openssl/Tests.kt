package openssl

import kotlin.test.Test
import klog.*


val log = klog("openssl") {
  writer = KLogWriters.stdOut
  messageFormatter = KMessageFormatters.verbose.colored
  level = Level.TRACE
}

class Tests {
  @Test
  fun test() {
    log.trace("test() trace message")
    log.debug("Test worked")
  }
}