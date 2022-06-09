package danbroid.kipfs

import danbroid.kipfs.jni.JNI
import danbroid.logging.DBLog

object KipfsLibJvm : KipfsLib {

  val log = danbroid.logging.configure("KIPFS", coloured = true).also {
    it.warn("configured logging")
/*    static DBLog log = danbroid.logging.LoggingKt.getLog("GODEMO");

    static {
      log.info("loading godemo..", null);
      System.loadLibrary("godemojni");
      log.info("loading godemojni..", null);
      System.loadLibrary("godemojni");
      log.debug("finished loading godemo libraries", null);
    }*/
  }

  init {

    runCatching {
      log.info("loading kipfsgo ..")
      System.loadLibrary("kipfsgo")
    }.exceptionOrNull()?.also {
      log.error(it.message,it)
    }

    runCatching {
      log.info("loading kipfs ..")
      System.loadLibrary("kipfs")
    }.exceptionOrNull()?.also {
      log.error(it.message,it)
    }
  }

  override fun getTime(): String = JNI.getTime()
  override fun dagCID(json: String): String = JNI.dagCID(json)


}


actual fun initKipfsLib(): KipfsLib = KipfsLibJvm

actual fun Any.log(): DBLog = KipfsLibJvm.log.let {
  danbroid.logging.getLog(this::class)
}



