@file:Suppress("UNUSED_PARAMETER")

package kipfs.api

import kipfs.KResponse
import kipfs.KShell
import kotlinx.serialization.Serializable

/**
 * Response to `ipfs id`
 */
@Serializable
data class ResponseID(
  val ID: String,
  val AgentVersion: String,
  val ProtocolVersion: String,
  val PublicKey: String,
  val Protocols: List<String>?,
  val Addresses: List<String>?,
)

/**
/api/v0/id

Show IPFS node id info.

Arguments

peerID [string]: Peer.ID of node to look up. Required: no.
peerIdBase [string]: Encoding used for peer IDs: Can either be a multibase encoded CID or a base58btc encoded multihash. Takes { b58mh|base36|k|base32|b... }. Default: b58mh. Required: no.
 
 **/

suspend inline fun KShell.id(peerID: String? = null): KResponse<ResponseID>
 = request<ResponseID>("id").send()


