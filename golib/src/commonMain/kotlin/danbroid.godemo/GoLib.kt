package danbroid.godemo

interface GoLib {
  fun getTime(): String
}

expect fun initGoLib(): GoLib