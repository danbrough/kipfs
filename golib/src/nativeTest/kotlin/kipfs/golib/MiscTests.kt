package kipfs.golib

import klog.klog
import kotlinx.cinterop.*
import kotlin.test.Test

class MiscTests {
  
  companion object {
    val log = klog()
  }
  
  @Test
  fun test() {
    log.info("pass_string test")
    kipfsgo.pass_string("String from kotlin".cstr)
  }
  
  @Test
  fun stringTests() {
    log.info("stringTests()")
    memScoped {
      log.debug("calling pass_string..")
      kipfsgo.pass_string("Here is a string".cstr)
      log.debug("calling return_string..")
      val s = kipfsgo.return_string()!!.toKString()
      log.info("received: $s")
      
      log.debug("calling copy_string...")
      val buf = ByteArray(255)
      buf.usePinned { pinned ->
        if (kipfsgo.copy_string(pinned.addressOf(0), buf.size - 1) != 0) {
          throw Error("Failed to read string from C")
        }
      }
      
      val copiedStringFromC = buf.toKString()
      log.warn("Message from C: -$copiedStringFromC-")
    }
  }
  
  
  @Test
  fun callbackTest() {
    log.info("callbackTest()")
    
    kipfsgo.KCallbackTest(staticCFunction { bytes, len, err ->
      err?.copyToKString()?.also {
        log.error(it)
        return@staticCFunction
      }
      val data = bytes?.reinterpret<ByteVar>()?.copyToKString()
      log.info("CALLBACK RECEIVED: $data len:$len")
    })
  }
  
  @Test
  fun structTest() {
    log.warn("structTest()")
    val cStruct = cValue<kipfsgo.MyStruct> {
      a = 42
      b = 3.14
    }
    
    kipfsgo.StructTest(cStruct)
  }
  
  @Test
  fun test2() {
    memScoped {
      log.warn("running test2()..")
      kipfsgo.KTest()?.also { cptr ->
        log.warn("got pointer $cptr")
        
        /*    cptr.reinterpret<ByteVar>().also {
          log.info("STRING: ${it.copyToString()}")
        }
*/
        cptr.readBytes(12).also { arr ->
          log.trace("read bytes length: ${arr.size}: ${arr.joinToString { "[${it.toUByte()}]" }}")
          
        }
        
        val str = cptr.reinterpret<ByteVar>().toKString()
        log.debug("str: $str")
        
      }
    }
  }
}