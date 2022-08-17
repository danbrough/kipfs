package kipfs.api

enum class MultibaseEncoding(val encoding: Char) {
  Identity(0.toChar()),
  Base2('0'),
  Base8('7'),
  Base10('9'),
  Base16('f'),
  Base16Upper('F'),
  Base32('b'),
  Base32Upper('B'),
  Base32pad('c'),
  Base32padUpper('C'),
  Base32hex('v'),
  Base32hexUpper('V'),
  Base32hexPad('t'),
  Base32hexPadUpper('T'),
  Base36('k'),
  Base36Upper('K'),
  Base58BTC('z'),
  Base58Flickr('Z'),
  Base64('m'),
  Base64url('u'),
  Base64pad('M'),
  Base64urlPad('U'),
  Base256Emoji(128640.toChar());

  companion object {
    fun valueOf(encoding: Int): MultibaseEncoding =
      values().first { it.encoding.code == encoding }
  }
}

data class MultibaseDecodeResult(val encoding: MultibaseEncoding, val data: ByteArray) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    
    other as MultibaseDecodeResult
    
    if (encoding != other.encoding) return false
    if (!data.contentEquals(other.data)) return false
    
    return true
  }
  
  override fun hashCode(): Int {
    var result = encoding.hashCode()
    result = 31 * result + data.contentHashCode()
    return result
  }
}
