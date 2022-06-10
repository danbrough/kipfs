package danbroid.kipfs.api

import danbroid.kipfs.KIPFS
import danbroid.kipfs.Shell
import danbroid.kipfs.decodeJson

/**
 * api/v0/dag/get
 * Get a dag node from ipfs.
 **/


suspend inline fun <reified T> Shell.dagGet(arg: String): T =
  request("dag/get", arg).decodeJson()




