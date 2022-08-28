package kipfs.golib
import kipfs.KIPFS
import kipfs.KResponse

interface KIPFSNative : KIPFS {
  fun createNativeShell(address: String): Int
  fun createRequest(shellRef:Int,command:String,arg:String? = null): Int
  fun disposeGoObject(ref: Int)
  fun request(shellRefID: Int, cmd: String, arg: String? = null): ByteArray
  fun requestOption(requestRefID:Int,name:String,value:String)
  fun sendRequest(requestRefID: Int): ByteArray
  fun <T> postString(shellRefID: Int,data:String): KResponse<T>
  fun <T> postData(shellRefID: Int,data:ByteArray): KResponse<T>
}