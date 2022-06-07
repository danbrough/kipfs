package danbroid.kipfs

interface KipfsLib {
  fun getTime(): String
}

expect fun initKipfsLib(): KipfsLib