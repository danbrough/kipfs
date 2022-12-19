package kipfs.golib

import klog.klog

open class KNativeObject(protected val kipfs: KIPFSNative) {
  
  private val log = klog()
  
  protected var ref = 0
  
  protected constructor(kipfs: KIPFSNative, ref:Int): this(kipfs){
    this.ref = ref
  }
  
  protected fun finalize() {
    //log.warn("finalize() $ref")
    close()
  }
  

  open fun close() {
    log.info("close() $ref")
    if (ref != 0) {
      kipfs.disposeGoObject(ref)
      ref = 0
    }
  }
}