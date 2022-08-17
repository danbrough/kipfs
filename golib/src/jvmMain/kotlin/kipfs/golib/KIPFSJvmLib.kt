package kipfs.golib

import kipfs.KIPFS
import kipfs.KShell
import kipfs.golib.jni.JNI
import klog.klog

private object KIPFSJvmLib : KIPFSNativeLib {
  
  val log = klog()
  
  init {
    
    runCatching {
      log.info("loading kipfsgo ..")
      System.loadLibrary("kipfsgo")
    }.exceptionOrNull()?.also {
      log.error(it.message, it)
    }
    
    runCatching {
      log.info("loading kipfs ..")
      System.loadLibrary("kipfs")
    }.exceptionOrNull()?.also {
      log.error(it.message, it)
    }
  }
  
  override fun createNativeShell(address: String): Int = JNI.createNativeShell(address)
  
  override fun disposeGoObject(ref: Int) = JNI.disposeGoObject(ref)
  override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray =
    JNI.request(shellRefID, cmd, arg)
  
  override fun createShell(ipfsAddress: String): KShell = KNativeShell(this, ipfsAddress)
  
  override fun environment(key: String): String? = System.getenv(key)
  
  override fun getTime(): String = JNI.getTime()
  override fun dagCID(json: String): String = JNI.dagCID(json)
  
  
}


actual fun initKIPFSLib(): KIPFS = KIPFSJvmLib



