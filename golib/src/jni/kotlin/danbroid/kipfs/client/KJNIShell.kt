package danbroid.kipfs.client

import KIPFSLibJNI
import KShell

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

  override fun id(): String {
    connect()
    TODO()
  }

  override fun toString(): String = "KShell[$ipfsAddress]"
}