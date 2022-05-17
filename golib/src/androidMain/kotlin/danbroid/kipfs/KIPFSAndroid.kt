package danbroid.kipfs

import android.util.Log
import danbroid.logging.StdOutLog

actual fun initKIPFSLib(): KIPFSLib {
  danbroid.logging.configure("TEST", defaultLog = StdOutLog, coloured = true)
  return KIPFSLibJNI
}

