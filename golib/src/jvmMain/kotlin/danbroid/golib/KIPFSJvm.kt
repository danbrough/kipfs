package danbroid.golib

import kipfs.KIPFS
import kipfs.KShell


actual fun initKIPFSLib(): KIPFS = object: KIPFS {
  override fun createShell(ipfsAddress: String): KShell {
    TODO("Not yet implemented")
  }

  override fun environment(key: String): String? {
    TODO("Not yet implemented")
  }

  override fun getTime(): String {
    TODO("Not yet implemented")
  }

  override fun dagCID(json: String): String {
    TODO("Not yet implemented")
  }

}



