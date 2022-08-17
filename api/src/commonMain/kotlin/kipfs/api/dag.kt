package kipfs.api

import kipfs.KResponse
import kipfs.KShell

/**
 * api/v0/dag/get
 * Get a dag node from ipfs.
 **/


suspend inline fun <reified T> KShell.dagGet(arg: String): KResponse<T> =
  request("dag/get", arg)




