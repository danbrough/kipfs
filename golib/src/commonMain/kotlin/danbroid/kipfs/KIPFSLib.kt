package danbroid.kipfs

import org.danbrough.klog.KLog
import org.danbrough.klog.klog


expect fun initKIPFSLib(): KIPFS

fun Any.log(): KLog = initKIPFSLib().let {
  this.klog()
}
