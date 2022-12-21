package demo

import klog.KLogWriters
import klog.KMessageFormatters
import klog.Level
import klog.colored

/*

private val log = klog {
  level = Level.TRACE
  writer = KLogWriters.stdOut
  messageFormatter = KMessageFormatters.verbose.colored
}

*/

val log = klog.klog("DEMO"){
  level = Level.TRACE
  writer = KLogWriters.stdOut
  messageFormatter = KMessageFormatters.verbose.colored
}
fun main(args:Array<String>){
  log.info("running demo...")
}

