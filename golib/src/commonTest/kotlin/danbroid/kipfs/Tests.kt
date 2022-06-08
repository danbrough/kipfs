package danbroid.kipfs

import danbroid.kipfs.initKipfsLib
import kotlin.test.Test

class Tests {
  @Test
  fun getTime() {
    val kipfsLib = initKipfsLib()
    println("kipfsLib.getTime() = ${kipfsLib.getTime()}")
  }
}
