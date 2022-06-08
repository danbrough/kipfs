package danbroid.kipfs

import danbroid.logging.DBLog

interface KipfsLib {
  fun getTime(): String

  fun dagCID(json:String):String
}

expect fun initKipfsLib(): KipfsLib
expect fun Any.log(): DBLog