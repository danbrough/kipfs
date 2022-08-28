package kipfs.api

import kipfs.KRequest
import kipfs.KResponse
import kipfs.KShell
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * api/v0/dag/get
 * Get a dag node from ipfs.
 **/


suspend inline fun <reified T> KShell.dagGet(arg: String): KResponse<T> =
  request<T>("dag/get", arg).send()


@Serializable
data class CID(
  @SerialName("/")
  val value: String
)

@Serializable
data class ResponseDagPut(@SerialName("Cid") val cid: CID)

/*
* api/v0/dag/put
* Add a DAG node to IPFS.

#Arguments
store-codec [string]: Codec that the stored object will be encoded with. Default: dag-cbor. Required: no.
input-codec [string]: Codec that the input object is encoded in. Default: dag-json. Required: no.
pin [bool]: Pin this object when adding. Required: no.
hash [string]: Hash function to use. Default: sha2-256. Required: no.
allow-big-block [bool]: Disable block size check and allow creation of blocks bigger than 1MiB. WARNING: such blocks won't be transferable over the standard bitswap. Default: false. Required: no.
#
    */

suspend inline fun KShell.dagPut(
  data: ByteArray,
  pin: Boolean = true,
  hash: String = "sha2-256",
  inputCodec: String = "dag-json",
  storeCodec: String = "dag-cbor",
  allowBigBlock: Boolean = false,
): KResponse<ResponseDagPut> =
  request<ResponseDagPut>("dag/put").apply {
    option("pin", pin)
    option("hash", hash)
    option("input-codec", inputCodec)
    option("store-codec", storeCodec)
    option("allow-big-block", allowBigBlock)
  }.post(data)
