import kotlinx.cinterop.*
import platform.linux.free
import kotlin.test.AfterClass
import kotlin.test.BeforeClass
import kotlin.test.Test

@ThreadLocal
var shellID: Int = 0

class NativeTests {

  companion object {


    @BeforeClass
    fun setupTests() {
      log.warn("setupTests()!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    @AfterClass
    fun tearDownTests() {
      log.warn("TEAR DOWN TESTS!!!!!!!!! shellID: $shellID")

      if (shellID != 0) {
        log.trace("disposing of shell")
        libkipfs.KDestroyRef(shellID)
        shellID = 0
      }
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
      log.trace("getting ipfs address from environment: $ENV_KIPFS_ADDRESS")
      val ipfsAddr = platform.posix.getenv(ENV_KIPFS_ADDRESS) ?: DEFAULT_KIPFS_ADDRESS.let {
        log.trace("Not found. Using default ipfs address $it")
        it.cstr
      }

      log.trace("creating shell to ${ipfsAddr.getPointer(this).toKString()}")

      shellID = libkipfs.KCreateShell(ipfsAddr).useContents {

        r1?.copyToString()?.let {
          log.error("An error occurred: $it")
          -1
        } ?: let {
          log.trace("ref is $r0")
          r0
        }
      }

      if (shellID == -1) return

      log.trace("calling id..")
      libkipfs.KRequest(shellID, "id".utf8).useContents {
        r2?.copyToString()?.also {
          throw Exception("Request failed: $it")
        } ?: run {
          r0!!.readBytes(r1.toInt()).decodeToString().also {
            log.info("RESULT: $it")
          }
        }
      }




      log.trace("finished")
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
}
