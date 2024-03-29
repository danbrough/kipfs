package kipfs.golib

import kipfsgo.PostDataType
import klog.klog
import kotlinx.cinterop.*
import kotlin.test.Test

class RequestTests : NativeTests() {
  
  private val shellID by lazy {
    log.warn("connecting to $IPFS_ADDRESS")
    kipfsgo.KCreateShell(IPFS_ADDRESS.cstr).useContents {
      r1?.copyToKString()?.also {
        throw Exception(it)
      }
      r0
    }
  }
  
  @Test
  fun idTest() {
    memScoped {
      log.trace("calling id at $IPFS_ADDRESS")
      
      val idTest = {
        log.warn("idTEST()")
        kipfsgo.KRequest(shellID, "id".utf8, null).useContents {
          r2?.copyToKString()?.also {
            throw Exception("Request failed: $it")
          } ?: run {
            r0!!.readBytes(r1.toInt()).decodeToString().also {
              log.info("RESULT: $it")
            }
          }
        }
      }
      
      idTest()
    }
  }
  
  
  @Test
  fun postTest() {
    memScoped {
      
      
      log.trace("calling multibase/encode at $IPFS_ADDRESS")
      
      val requestRefId = kipfsgo.KCreateRequest(shellID, "id".utf8, null).useContents {
        r1?.copyToKString()?.also {
          throw Exception("Create request failed: $it")
        }
        r0
      }
      println("Got request id $requestRefId")
      kipfsgo.KRequestSend(requestRefId).useContents {
        kipfsgo.KDecRef(requestRefId)
  
        r2?.copyToKString()?.also {
          throw Exception("Send request failed: $it")
        } ?: r0!!.readBytes(r1.toInt()).decodeToString().also {
          log.info("RESULT: $it")
        }
        
      }
  

      println("dec ref done")

/*
      kipfsgo.KPostRequest(
        shellID,
        "multibase/encode".utf8,
        null,
        "123".utf8,
        kipfsgo.PostDataType.StringFile
      ).useContents {
        r2?.copyToKString()?.also {
          throw Exception("Request failed: $it")
        } ?: run {
          r0!!.readBytes(r1).decodeToString().also {
            log.info("RESULT: $it")
          }
        }
      }*/
      
    }
    
  }
}