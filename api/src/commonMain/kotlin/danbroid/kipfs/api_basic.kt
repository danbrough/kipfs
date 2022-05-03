package danbroid.kipfs

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