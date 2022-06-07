package danbroid.godemo

object GoLibJvm : GoLib {

  val log = danbroid.logging.configure("GODEMO", coloured = true).also {
    it.warn("configured logging")
  }

  override fun getTime(): String = JNI.getTime()


}


actual fun initGoLib(): GoLib = GoLibJvm



