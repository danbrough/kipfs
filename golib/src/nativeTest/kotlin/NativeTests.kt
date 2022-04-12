import kotlinx.cinterop.*
import platform.linux.free
import kotlin.test.Test

class NativeTests {


  @Test
  fun test2() {
    memScoped {
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

      val refNum = libkipfs.KCreateShell(ipfsAddr).useContents {

        r1?.copyToString()?.let {
          log.error("An error occurred: $it")
          -1
        } ?: let {
          log.trace("ref is $r0")
          r0
        }
      }

      if (refNum == -1) return

      log.trace("calling id..")
      libkipfs.KRequest(refNum, "id".utf8).useContents {
        r2?.copyToString()?.also {
          throw Exception("Request failed: $it")
        } ?: run {
          r0!!.readBytes(r1.toInt()).decodeToString().also {
            log.info("RESULT: $it")
          }
        }
      }



      log.trace("disposing of shell")
      libkipfs.KDestroyRef(refNum)
      log.trace("finished")
    }
  }
}
