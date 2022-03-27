interface KIPFS {
  fun getMessage(): String
  fun getMessage2(): String
  fun dagCID(json: String): String
}

expect fun initLib(): KIPFS




