package kipfs.golib

import kipfs.KIPFS
import kipfs.KResponse
import kipfs.KShell


actual fun initKIPFSLib(): KIPFS = JniInstance

private object JniInstance : KIPFSJni()

open class KIPFSJni : KIPFSNative {

  //private val log = klog.klog(KIPFSJni::class)
  init {
    runCatching {
      System.loadLibrary("kipfsgo")
      println("loaded kipfsgo")
    }.exceptionOrNull()?.also {
      println("ERROR: ${it.message}")
      throw it
    }

    runCatching {
      System.loadLibrary("kipfs")
      println("loaded kipfs")
    }.exceptionOrNull()?.also {
      println("ERROR: ${it.message}")
      throw it
    }
    println("finished loading native libraries")
  }

  external override fun createNativeShell(address: String): Int

  external override fun disposeGoObject(ref: Int)

  external override fun createRequest(shellRef: Int, command: String, arg: String?): Int

  override fun requestOption(requestRefID: Int, name: String, value: String) {
    TODO("Not yet implemented")
  }

  external override fun sendRequest(requestRefID: Int): ByteArray

  override fun <T> postString(shellRefID: Int, data: String): KResponse<T> {
    TODO("Not yet implemented")
  }

  override fun <T> postData(shellRefID: Int, data: ByteArray): KResponse<T> {
    TODO("Not yet implemented")
  }

  override fun createShell(ipfsAddress: String): KShell = KNativeShell(this, ipfsAddress)

  override fun environment(key: String): String? = System.getenv(key)


  external override fun getTime(): String


  external override fun dagCID(json: String): String

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