package kipfs.golib

import kipfs.KIPFS
import kipfs.KShell
import java.util.Date


actual fun initKIPFSLib(): KIPFS = KIPFSJni

object KIPFSJni: KIPFS {

  private val log = klog.klog(KIPFSJni::class)
  init {
    log.info("INIT KIPFSJni")
    runCatching {
      log.debug("loading kipfsgo ..")
      System.loadLibrary("kipfsgo")
    }
    log.debug("finished loading native libraries")
  }

  override fun createShell(ipfsAddress: String): KShell {
    TODO("Not yet implemented")
  }

  override fun environment(key: String): String? {
    TODO("Not yet implemented")
  }

  external override fun getTime(): String

  external fun getMessage(): String


  override fun dagCID(json: String): String {
    TODO("Not yet implemented")
  }

}

/*

 */



/*

object KIPFSLibJNI : KIPFSNativeLib {
  private val log= danbroid.logging.configure("TEST", coloured = true)

  init {
    log.info("INIT KIPFSLibJNI")
    runCatching {
      log.debug("loading gokipfs ..")
      System.loadLibrary("gokipfs")
    }
    log.debug("loading kipfs ..")
    System.loadLibrary("kipfs")
    log.debug("finished loading native libraries")
  }


  external override fun getMessage(): String
  external override fun getMessage2(): String
  external override fun dagCID(json: String): String
  external override fun createNativeShell(address: String): Int
  external override fun disposeGoObject(ref: Int)
  external override fun request(shellRefID: Int, cmd: String, arg: String?): ByteArray

  override fun createShell(ipfsAddress: String): Shell = KNativeShell(this, ipfsAddress)

  override fun environment(key: String): String? = System.getenv(key)

}



 */