package kipfs.golib

import kipfs.DEFAULT_KIPFS_ADDRESS
import kipfs.ENV_KIPFS_ADDRESS
import klog.*
import kotlinx.cinterop.toKString

open class NativeTests {
  companion object {
    private val log = klog("kipfs.golib") {
      level = Level.TRACE
      messageFormatter = KMessageFormatters.verbose.colored
      writer = KLogWriters.stdOut
    }
    
    val IPFS_ADDRESS =
      platform.posix.getenv(ENV_KIPFS_ADDRESS)?.toKString() ?: DEFAULT_KIPFS_ADDRESS.also {
        log.warn("Environment variable $ENV_KIPFS_ADDRESS not set. Using default ipfs address $DEFAULT_KIPFS_ADDRESS")
      }
  }
  
  val log = klog()
  
}

