package org.danbrough.kipfs.jni;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JNI {

  public static native String getTime();

  public static native String dagCID(String json);

  public static native int createNativeShell(String address);

  public static native void disposeGoObject(int refnum);

  @NotNull
  public static native byte[] request(int shellRefID, @NotNull String cmd, @Nullable String arg);
}
