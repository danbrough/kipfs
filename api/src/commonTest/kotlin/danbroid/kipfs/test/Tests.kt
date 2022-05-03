package danbroid.kipfs.test

import KIPFSLib
import KShell
import danbroid.kipfs.KIPFS
import danbroid.kipfs.ResponseID
import danbroid.kipfs.decodeJson
import initKIPFSLib
import kotlin.test.Test
import kotlinx.serialization.Serializable

val log = danbroid.logging.configure("TEST", coloured = true)

interface KIPFSTest {
  val kipfs: KIPFSLib
  val shell: KShell
}

object TestConfig : KIPFSTest {
  override val kipfs = initKIPFSLib()

  val ipfsAddress = "/ip4/192.168.1.4/tcp/5001"

  override val shell: KShell by lazy {
    kipfs.createShell(ipfsAddress)
  }
}

@Serializable
data class Thang(val id: String)


class Tests : KIPFSTest by TestConfig {
  @Test
  fun test1() {
    log.info("test1()")
    shell.id().also {
      log.debug("ID: $it")
      val response = it.decodeJson<ResponseID>()
      log.trace("response: $response")
    }

  }
}