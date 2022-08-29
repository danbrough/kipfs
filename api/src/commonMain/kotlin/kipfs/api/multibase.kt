package kipfs.api


import kipfs.KResponse
import kipfs.KShell
import kipfs.MultibaseEncoding

/**
 * /api/v0/multibase/encode
 * Encode data into multibase string
 *
 * b [string]: multibase encoding. Default: base64url. Required: no.
 *
 **/


suspend inline fun KShell.multibaseEncode(data: String, encoding: MultibaseEncoding = MultibaseEncoding.Base64url): KResponse<String> =
  request<String>("multibase/encode").option("b",encoding.name.lowercase()).post(data.encodeToByteArray())




