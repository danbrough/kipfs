package danbroid.kipfs

const val ENV_KIPFS_ADDRESS = "KIPFS_ADDRESS"
const val DEFAULT_KIPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001"


interface Shell {
  fun connect()
  fun close()
  suspend fun request(command: String, arg: String? = null): ByteArray
}

interface KIPFS {
  fun createShell(ipfsAddress: String = DEFAULT_KIPFS_ADDRESS): Shell

  fun environment(key: String): String?
}

