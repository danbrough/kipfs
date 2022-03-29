
interface  KShell {

}

interface KIPFSLib {
  fun getMessage(): String
  fun getMessage2(): String
  fun dagCID(json: String): String
  fun createShell(url:String): KShell
}


expect fun initKIPFSLib(): KIPFSLib




