package danbroid.kipfs

object KipfsLibJvm : KipfsLib {

  val log = danbroid.logging.configure("GODEMO", coloured = true).also {
    it.warn("configured logging")
  }

  override fun getTime(): String = JNI.getTime()


}


actual fun initKipfsLib(): KipfsLib = KipfsLibJvm



