package kipfs.golib

import klog.klog
import kotlinx.cinterop.*
import kotlin.test.Test

class RequestTests : NativeTests() {
  
  @Test
  fun idTest() {
    memScoped {
      log.trace("calling id at $IPFS_ADDRESS")
      val shellID = kipfsgo.KCreateShell(IPFS_ADDRESS.cstr).useContents {
        r1?.copyToKString()?.also {
          throw Exception(it)
        }
        r0
      }
      log.debug("shellID: $shellID")
      
      
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
}