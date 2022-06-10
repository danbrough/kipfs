package danbroid.kipfs

import danbroid.logging.DBLog

interface KIPFSLib {
  fun getTime(): String

  fun dagCID(json: String): String
}



expect fun initKIPFSLib(): KIPFSLib

fun Any.log(): DBLog = initKIPFSLib().let {
  danbroid.logging.getLog(this::class)
}
