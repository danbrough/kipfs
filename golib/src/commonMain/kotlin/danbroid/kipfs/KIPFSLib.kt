package danbroid.kipfs

import klog.KLog
import klog.klog


expect fun initKIPFSLib(): KIPFS

fun Any.log(): KLog = initKIPFSLib().let {
  this.klog()
}
