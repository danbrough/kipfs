package danbroid.kipfs.client

import danbroid.kipfs.KIPFSLibJNI
import danbroid.kipfs.KShell


class KJNIShell(private val ipfsAddress: String) : KShell {

  companion object {
    private val log = danbroid.logging.getLog(KJNIShell::class)
  }

  private var ref = 0

  protected fun finalize() {
    log.warn("finalize() $ref")
    dispose()
  }

  override fun connect() {
    if (ref != 0) return
    log.info("connect() $ipfsAddress")
    ref = KIPFSLibJNI.createShellJNI(ipfsAddress)
  }

  override fun dispose() {
    log.info("dispose() $ref")
    ref = 0
  }

  override fun request(command: String, arg: String?): ByteArray {
    connect()
    return KIPFSLibJNI.request(ref, command, arg)
  }


  override fun toString(): String = "KShell[$ipfsAddress]"
}