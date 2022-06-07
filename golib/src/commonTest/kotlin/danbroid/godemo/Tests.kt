package danbroid.godemo

import danbroid.godemo.initGoLib
import kotlin.test.Test

class Tests {
  @Test
  fun getTime() {
    val golib = initGoLib()
    println("The time is: ${golib.getTime()}")
  }
}