import KIPFSLibNative.log
import danbroid.kipfs.ResponseID
import danbroid.kipfs.decodeJson
import kotlinx.cinterop.*
import platform.linux.free
import kotlin.test.AfterClass
import kotlin.test.BeforeClass
import kotlin.test.Test

private fun CPointer<ByteVar>.convertToString(): String = this.toKString().also {
  platform.posix.free(this)
}

private val ipfsAddress =
  platform.posix.getenv(ENV_KIPFS_ADDRESS)?.toKString() ?: DEFAULT_KIPFS_ADDRESS.also {
    log.warn("environment ENV_KIPFS_ADDRESS not set. Using default ipfs address $it")
  }


class NativeTests {

  companion object {


    @BeforeClass
    fun setupTests() {
      log.info("setupTests()")


    }

    @AfterClass
    fun tearDownTests() {
      // log.warn("TEAR DOWN TESTS!!!!!!!!! shellID: $shellID")

    }
  }


  @Test
  fun test2() {
    memScoped {
      log.warn("running test2()..")
      libkipfs.KTest()?.also { cptr ->
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

/*  @Test
  fun test3() {
    memScoped {
      val response = libkipfs.KRequest2("id".cstr)
      log.trace("got response: $response")
      response.useContents {
        r2?.copyToString()?.also {
          log.error("ERROR: $it")
          return@memScoped
        }
        r0?.reinterpret<ByteVar>()?.copyToString()?.also {
          log.info("RESPONSE: $it len: $r1")
        }
      }

    }
  }*/

  @Test
  fun printerTest() {
    libkipfs.print_test("Dude!")
  }

  @Test
  fun shellTest() {
    memScoped {
      log.trace("calling id at $ipfsAddress")
      val shellID = libkipfs.KCreateShell(ipfsAddress.cstr).useContents {
        r1?.convertToString()?.also {
          throw Exception(it)
        }
        r0
      }

      libkipfs.KRequest(shellID, "id".utf8,null).useContents {
        r2?.convertToString()?.also {
          throw Exception("Request failed: $it")
        } ?: run {
          r0!!.readBytes(r1.toInt()).decodeToString().also {
            log.info("RESULT: $it")
            it.decodeJson<ResponseID>().also {
              log.trace("RESPONSE: $it")
            }
          }
        }
      }
      log.info("finished .. disposing of shell")
      libkipfs.KDestroyRef(shellID)
    }
  }

  @Test
  fun stringTests() {

    memScoped {
      log.debug("calling pass_string..")
      libkipfs.pass_string("Here is a string".cstr)
      log.debug("calling return_string..")
      val s = libkipfs.return_string()!!.toKString()
      log.info("received: $s")

      log.debug("calling copy_string...")
      val buf = ByteArray(255)
      buf.usePinned { pinned ->
        if (libkipfs.copy_string(pinned.addressOf(0), buf.size - 1) != 0) {
          throw Error("Failed to read string from C")
        }
      }

      val copiedStringFromC = buf.toKString()
      println("Message from C: -$copiedStringFromC-")
    }
  }

  @Test
  fun callbackTest() {
    log.info("callbackTest()")

    libkipfs.KCallbackTest(staticCFunction { bytes, len, err ->
      err?.convertToString()?.also {
        log.error(it)
        return@staticCFunction
      }
      val data = bytes?.reinterpret<ByteVar>()?.convertToString()
      log.info("CALLBACK RECEIVED: $data len:$len")
    })
  }


}
