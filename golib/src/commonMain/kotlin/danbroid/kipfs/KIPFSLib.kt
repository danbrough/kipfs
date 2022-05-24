package danbroid.kipfs


interface KIPFSLib : KIPFS {
  fun getMessage(): String
  fun getMessage2(): String
  fun dagCID(json: String): String

}

interface KIPFSNativeLib : KIPFSLib {
  fun createNativeShell(address: String): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray
}

expect fun initKIPFSLib(): KIPFSLib




