
interface KIPFSLib {
  fun getMessage(): String
  fun getMessage2(): String
  fun dagCID(json: String): String
}

expect fun initKIPFSLib(): KIPFSLib




