package danbroid.kipfs

import danbroid.logging.DBLog




expect fun initKIPFSLib(): KIPFS

fun Any.log(): DBLog = initKIPFSLib().let {
  danbroid.logging.getLog(this::class)
}
