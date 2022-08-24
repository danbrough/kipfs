package kipfs.api


import kipfs.KResponse
import kipfs.KShell

/**
 * /api/v0/multibase/encode
 * Encode data into multibase string
 **/


suspend inline fun <reified T> KShell.multibaseEncode(arg: String): KResponse<T> =
  request("multibase/encode", arg)




