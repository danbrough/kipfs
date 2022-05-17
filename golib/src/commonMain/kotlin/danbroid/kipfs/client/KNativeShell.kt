package danbroid.kipfs.client

import danbroid.kipfs.KIPFSNativeLib
import danbroid.kipfs.KShell


class KNativeShell(private val kipfs: KIPFSNativeLib, private val ipfsAddress: String) : KShell {

  companion object {
    private val log = danbroid.logging.getLog(KNativeShell::class)
  }

  private var ref = 0

  protected fun finalize() {
    log.warn("finalize() $ref")
    dispose()
  }

  override fun connect() {
    if (ref != 0) return
    log.info("connect() $ipfsAddress")
    ref = kipfs.createNativeShell(ipfsAddress)
  }

  override fun dispose() {
    log.info("dispose() $ref")
    if (ref != 0) {
      kipfs.disposeGoObject(ref)
      ref = 0
    }
  }

  override fun request(command: String, arg: String?): ByteArray {
    connect()
    return kipfs.request(ref, command, arg)
  }

  override fun toString(): String = "KShell[$ipfsAddress]"
}