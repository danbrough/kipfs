package danbroid.kipfs

import android.util.Log
import danbroid.logging.StdOutLog

actual fun initKIPFSLib(): KIPFSLib {
  try {
    Log.d("LOGGING", "configuring logging")
    danbroid.logging.configure("TEST", coloured = true)
  } catch (err: Throwable) {
    danbroid.logging.configure("TEST", defaultLog = StdOutLog, coloured = true)
  }
  return KIPFSLibJNI
}

