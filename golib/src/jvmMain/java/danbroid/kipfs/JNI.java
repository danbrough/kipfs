package danbroid.kipfs;

import danbroid.logging.DBLog;

public class JNI {

  static DBLog log = danbroid.logging.LoggingKt.getLog("GODEMO");

  static {
    log.info("loading godemo..", null);
    System.loadLibrary("godemojni");
    log.info("loading godemojni..", null);
    System.loadLibrary("godemojni");
    log.debug("finished loading godemo libraries", null);
  }

  public static native String getTime();
}
