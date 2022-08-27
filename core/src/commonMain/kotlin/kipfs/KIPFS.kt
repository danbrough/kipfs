package kipfs


const val ENV_KIPFS_ADDRESS = "KIPFS_ADDRESS"
const val DEFAULT_KIPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001"

interface KResponse<T>{
  fun readAll(): ByteArray
}

interface KRequest<T>{
  fun option(name:String,value:Any): KRequest<T>
  fun post(data:String,fileName:String = ""): KResponse<T>
  fun send(): KResponse<T>
}


class KByteResponse<T>(val data: ByteArray) : KResponse<T>{
  override fun readAll(): ByteArray = data
}

interface KShell {
  
  
  fun connect()
  
  fun close()
  
  //suspend fun <T> request(command: String, arg: String? = null): KResponse<T>
  
  suspend fun <T> request(command: String,arg: String? = null): KRequest<T>
  
  
}


interface KIPFS {
  
  fun createShell(ipfsAddress: String = DEFAULT_KIPFS_ADDRESS): KShell

  fun environment(key: String): String?

  fun getTime(): String
  
  fun dagCID(json: String): String
}

