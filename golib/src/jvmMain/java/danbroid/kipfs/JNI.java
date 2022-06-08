package danbroid.kipfs;

import danbroid.logging.DBLog;

public class JNI {



  public static native String getTime();

  public static native String dagCID(String json);
}
