package danbroid.kipfs.jni;


public class JNI {

  public static native String getTime();

  public static native String dagCID(String json);
}
