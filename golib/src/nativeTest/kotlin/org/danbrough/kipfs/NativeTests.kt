package org.danbrough.kipfs

import org.danbrough.kipfs.api.MultibaseDecodeResult
import org.danbrough.kipfs.api.MultibaseEncoding
import kotlinx.cinterop.cstr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.useContents
import org.danbrough.klog.*
import kotlin.test.Test

class NativeTests {

  private val log = klog(level = Level.TRACE, formatter = KLogFormatters.verbose.colored, writer = KLogWriters.stdOut)


  private fun multibaseEncode(encoding: MultibaseEncoding, data: ByteArray): String =
    kipfs.KMultiBaseEncode(encoding.encoding.code, data.toCValues(), data.size).useContents {
      r1?.also {
        throw Exception(it.copyToKString())
      }
      r0!!.copyToKString()
    }


  private fun multibaseDecode(data: String): MultibaseDecodeResult =
    kipfs.KMultiBaseDecode(data.cstr, data.length).useContents {
      r3?.copyToKString()?.also {
        throw Exception(it)
      }
      MultibaseDecodeResult(MultibaseEncoding.valueOf(r0.toInt()), r1!!.readBytes(r2))
    }


  @Test
  fun test1() {
    val encoding = MultibaseEncoding.Base64url
    listOf("testing", "123", "\u0000\u0001\u0002").forEach {
      val result = multibaseEncode(encoding, it.encodeToByteArray())
      log.info("$it => $result")

      val decoded = multibaseDecode(result)
      log.debug("decoded: <$decoded>")

    }
  }

}