package danbroid.kipfs

import danbroid.kipfs.api.MultibaseEncoding
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.useContents
import kotlin.test.Test

class NativeTests {

  val log = log()


  fun multibaseEncode(encoding: MultibaseEncoding, data: ByteArray): String =
    kipfs.KMultiBaseEncode(encoding.encoding.code, data.toCValues(), data.size).useContents {
      r1?.also {
        throw Exception(it.copyToKString())
      }
      r0!!.copyToKString()
    }


  @Test
  fun test1() {
    val encoding = MultibaseEncoding.Base58Flickr
    listOf("testing", "123", "\u0000\u0001\u0002").forEach {
      val result = multibaseEncode(encoding, it.encodeToByteArray())
      log.info("$it => $result")
    }
  }

}