package danbroid.kipfs

interface Shell {
  fun connect()
  fun close()
  suspend fun request(command: String, arg: String? = null): ByteArray


}

interface KIPFS {
  fun createShell(ipfsAddress: String = DEFAULT_KIPFS_ADDRESS): Shell

  fun environment(key: String): String?
}

