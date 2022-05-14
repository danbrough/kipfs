import kotlin.test.Test


class JvmTests {

  private val ipfsAddress by lazy {
    System.getenv(ENV_KIPFS_ADDRESS)?.also {
      log.warn("USING $ENV_KIPFS_ADDRESS from environment: $it")
      return@lazy it
    }

    DEFAULT_KIPFS_ADDRESS.also {
      log.warn("using $ENV_KIPFS_ADDRESS not set. Using default address: $it")
    }
  }

  @Test
  fun jvmTest() {
    log.info("jvmTest()")

    log.debug("connecting to $ipfsAddress")
    val refnum = KIPFSLibJNI.createShellJNI(ipfsAddress)

    KIPFSLibJNI.disposeGoObject(refnum)
  }

  @Test
  fun requestID() {
    log.info("requestID()")
    val shell = KIPFSLibJNI.createShell(ipfsAddress)
    shell.id().also {
      log.trace(it)
    }
  }


}