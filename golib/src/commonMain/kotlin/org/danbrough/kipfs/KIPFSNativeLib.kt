package org.danbrough.kipfs

interface KIPFSNativeLib : KIPFS {
  fun createNativeShell(address: String): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray

}