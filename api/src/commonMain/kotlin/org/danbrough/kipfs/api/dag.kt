package org.danbrough.kipfs.api

import org.danbrough.kipfs.KShell
import org.danbrough.kipfs.decodeJson

/**
 * api/v0/dag/get
 * Get a dag node from ipfs.
 **/


suspend inline fun <reified T> KShell.dagGet(arg: String): T =
  request("dag/get", arg).decodeJson()




