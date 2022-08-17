package org.danbrough.kipfs

import kipfs.KResponse
import kipfs.KShell
import klog.klog

class KNativeShell(private val kipfs: KIPFSNativeLib, private val ipfsAddress: String) : KShell {
  
  class KByteResponse<T>(val data:ByteArray): KResponse<T>
  
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
  
  override suspend fun <T> request(command: String, arg: String?): KResponse<T> {
    connect()
    return KByteResponse(kipfs.request(ref, command, arg))
  }
  
  override fun toString(): String = "KShell[$ipfsAddress]"
}