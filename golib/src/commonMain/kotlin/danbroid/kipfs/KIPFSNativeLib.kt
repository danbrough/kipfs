package danbroid.kipfs

interface KIPFSNativeLib : KIPFSLib {
  fun createNativeShell(address: String): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray
  fun environment(key: String): String?
}