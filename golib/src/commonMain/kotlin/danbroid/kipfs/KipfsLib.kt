package danbroid.kipfs

import danbroid.logging.DBLog

interface KipfsLib {
  fun getTime(): String

  fun dagCID(json: String): String
}

interface KIPFSNativeLib : KipfsLib {
  fun createNativeShell(address: String): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray
  fun environment(key: String): String?
}

expect fun initKipfsLib(): KipfsLib
fun Any.log(): DBLog = initKipfsLib().let {
  danbroid.logging.getLog(this::class)
}
