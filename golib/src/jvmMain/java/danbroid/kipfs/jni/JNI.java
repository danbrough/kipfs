package danbroid.kipfs.jni;


public class JNI {

  public static native String getTime();

  public static native String dagCID(String json);

  public static native int createNativeShell(String address);

  public static native void disposeGoObject(int refnum);
}
