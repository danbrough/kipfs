package danbroid.kipfs

import danbroid.kipfs.api.MultibaseEncoding
import kipfs.GoInt
import kipfs.GoInt32
import kipfs.GoInt64
import kotlinx.cinterop.cstr
import kotlinx.cinterop.useContents
import kotlin.test.Test

class NativeTests {

  val log = log()

  @Test
  fun test1() {
    val text = "testing"

    kipfs.KMultiBaseEncode(MultibaseEncoding.Base64url.code, text.cstr, text.length).useContents {
      r1?.also {
        throw Exception(it.copyToKString())
      }
      r0!!.copyToKString()
    }.also {
      log.info("returned <$it>")
    }
  }

}