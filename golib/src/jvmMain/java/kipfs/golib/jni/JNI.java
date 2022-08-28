package kipfs.golib.jni;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JNI {

  public static native String getTime();

  public static native String dagCID(String json);

  public static native int createNativeShell(String address);

  public static native void disposeGoObject(int refnum);

  @NotNull
  public static native byte[] request(int shellRefID, @NotNull String cmd, @Nullable String arg);

  public static native int createRequest(int shellRef, @NotNull String command,String arg);

  public static native void requestOption(int requestRefID,  String name, String value);

  @NotNull
  public static native byte[] sendRequest(int requestRefID);
}
