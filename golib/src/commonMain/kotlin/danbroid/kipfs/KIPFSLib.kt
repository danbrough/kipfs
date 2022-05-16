package danbroid.kipfs

const val ENV_KIPFS_ADDRESS = "KIPFS_ADDRESS"
const val DEFAULT_KIPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001"

interface KShell {
  fun connect()
  fun dispose()
  fun request(command: String, arg: String? = null): ByteArray

}

interface KIPFSLib {
  fun getMessage(): String
  fun getMessage2(): String
  fun dagCID(json: String): String
  fun createShell(url: String): KShell
  fun environment(key: String): String?
}


expect fun initKIPFSLib(): KIPFSLib




