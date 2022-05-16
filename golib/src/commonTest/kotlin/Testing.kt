import danbroid.kipfs.DEFAULT_KIPFS_ADDRESS
import danbroid.kipfs.ENV_KIPFS_ADDRESS
import danbroid.kipfs.KIPFSLib
import danbroid.kipfs.initKIPFSLib
import kotlin.test.Test


class Testing {

  companion object {
    const val CID_HELLO_WORLD = "QmSXfamUcvkEyYbtGdQdwZ4TrnVbneKrY5zNx7eGV5ckyq"

    val kipfs: KIPFSLib = initKIPFSLib()

    val log = danbroid.logging.getLog(Testing::class)

    val ipfsAddress: String by lazy {
      kipfs.environment(ENV_KIPFS_ADDRESS)?.also {
        log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
      } ?: DEFAULT_KIPFS_ADDRESS.also {
        log.warn("using $ENV_KIPFS_ADDRESS not set. Using default address: $it")
      }
    }
  }


  @Test
  fun test() {
    log.info("test1() ..")
    println("test()")

    log.debug(kipfs.getMessage())
    log.debug(kipfs.getMessage2())

    log.trace("DAG ${kipfs.dagCID("\"Hello World\"")}")
  }

  @Test
  fun cidAccess() {
    log.info("cidAccess()")
  }


}