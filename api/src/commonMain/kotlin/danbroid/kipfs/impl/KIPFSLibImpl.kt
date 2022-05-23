package danbroid.kipfs.impl

import danbroid.kipfs.*


class KIPFSShell(val kShell: KShell) : Shell {
  override fun connect() = kShell.connect()

  override fun close() = kShell.dispose()

  override suspend fun request(command: String, arg: String?): ByteArray = kShell.request(command, arg)

  protected fun finalize(){
    close()
  }

}

class KIPFSLibImpl(val kipfsLib: KIPFSLib) : KIPFS {
  override fun createShell(ipfsAddress: String): Shell =
    KIPFSShell(kipfsLib.createShell(ipfsAddress))

  override fun environment(key: String): String?  = kipfsLib.environment(key)

}