import kotlinx.coroutines.*

class Thang(val atEnd: () -> Unit) {
  protected fun finalize() {
    println("Finalize")
    atEnd()
  }
}

fun main() {

  val log = danbroid.logging.configure("TEST", coloured = true)
  val kipfs = initKIPFSLib()

  runBlocking {
    log.warn("message: ${kipfs.getMessage()}")



    kipfs.dagCID("\"Hello World\"").also {
      log.debug("cid: $it")
    }


  }


}