package kipfs.golib


import kipfs.KByteResponse
import kipfs.KRequest
import kipfs.KResponse
import kipfs.KShell

import klog.klog

class KNativeShell(kipfs: KIPFSNative, private val ipfsAddress: String) : KNativeObject(kipfs),
  KShell {
  
  private val log = klog()
  
  inner class KNativeRequest<T>(ref: Int) : KNativeObject(kipfs, ref), KRequest<T> {
    
    override fun send(): KResponse<T> = KByteResponse(kipfs.sendRequest(ref))
    
    
    override fun post(data: String, fileName: String): KResponse<T>
     = kipfs.postString(ref,data)
  
    override fun post(data: ByteArray): KResponse<T>  =
      kipfs.postData(ref,data)
    
    override fun option(name: String, value: Any): KRequest<T> {
      kipfs.requestOption(ref, name, value.toString())
      return this
    }
  }
  
  override fun connect() {
    if (ref != 0) return
    log.info("connect() $ipfsAddress")
    ref = kipfs.createNativeShell(ipfsAddress)
  }
  
  override suspend fun <T> request(command: String, arg: String?): KRequest<T> {
    connect()
    return KNativeRequest(kipfs.createRequest(ref, command, arg))
  }
  
  
  override fun toString(): String = "KShell[$ipfsAddress]"
}