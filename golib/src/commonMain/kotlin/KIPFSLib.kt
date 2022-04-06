
const val ENV_IPFS_ADDRESS = "IPFS_ADDRESS"
const val DEFAULT_IPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001"

interface KShell {
  fun id(): String
}

interface KIPFSLib {

  fun getMessage(): String
  fun getMessage2(): String
  fun dagCID(json: String): String
  fun createShell(url: String): KShell
}


expect fun initKIPFSLib(): KIPFSLib




