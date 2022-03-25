package danbroid.jvmdemo


import danbroid.mpp.HelloJNI
import danbroid.mpp.NativeLoader


class Demo1 {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val log = danbroid.logging.configure("TEST", coloured = true)

      log.info("test1() here!")
      NativeLoader.loadLibrary(Demo1::class.java.classLoader!!, "jnidemo")
      log.debug("HEllo: ${HelloJNI.stringFromJNI()}")
      log.warn("getMessage() = ${HelloJNI.getMessage()}")
      log.info("load finished!")

    }
  }
}