package danbroid.kipfs

import danbroid.logging.DBLog

interface KipfsLib {
  fun getTime(): String
}

expect fun initKipfsLib(): KipfsLib
expect fun Any.log(): DBLog