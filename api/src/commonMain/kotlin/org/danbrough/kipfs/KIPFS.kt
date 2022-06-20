package org.danbrough.kipfs

const val ENV_KIPFS_ADDRESS = "KIPFS_ADDRESS"
const val DEFAULT_KIPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001"


interface KShell {
  fun connect()
  fun close()
  suspend fun request(command: String, arg: String? = null): ByteArray
}

interface KIPFS {
  fun createShell(ipfsAddress: String = DEFAULT_KIPFS_ADDRESS): KShell

  fun environment(key: String): String?

  fun getTime(): String
  fun dagCID(json: String): String
}

