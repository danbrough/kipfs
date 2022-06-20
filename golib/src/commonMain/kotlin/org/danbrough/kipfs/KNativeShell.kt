package org.danbrough.kipfs

import org.danbrough.klog.klog

class KNativeShell(private val kipfs: KIPFSNativeLib, private val ipfsAddress: String) : KShell {

  private val log = klog()

  private var ref = 0

  protected fun finalize() {
    //log.warn("finalize() $ref")
    close()
  }

  override fun connect() {
    if (ref != 0) return
    log.info("connect() $ipfsAddress")
    ref = kipfs.createNativeShell(ipfsAddress)
  }

  override fun close() {
    log.info("close() $ref")
    if (ref != 0) {
      kipfs.disposeGoObject(ref)
      ref = 0
    }
  }

  override suspend fun request(command: String, arg: String?): ByteArray {
    connect()
    return kipfs.request(ref, command, arg)
  }

  override fun toString(): String = "KShell[$ipfsAddress]"
}