package kipfs.serialization

import kipfs.KResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.jvm.JvmName

@OptIn(ExperimentalSerializationApi::class)
private val cbor = Cbor {
  encodeDefaults = true
}

private val json = Json {
  encodeDefaults = true
}


fun <T> decodeJson(data: String, serializer: KSerializer<T>): T =
  json.decodeFromString(serializer, data)

fun <T> encodeJson(data: T, serializer: KSerializer<T>): String =
  json.encodeToString(serializer, data)

inline fun <reified T> T.encodeJson(): String = encodeJson(this, serializer())

@JvmName("encodeJsonWithSerializer")
fun <T> T.encodeJson(serializer: KSerializer<T>): String = encodeJson(this, serializer)

inline fun <reified T> String.decodeJson(): T = decodeJson(this, serializer())

@JvmName("decodeJsonWithSerializer")
fun <T> String.decodeJson(serializer: KSerializer<T>): T = decodeJson(this, serializer)

@JvmName("decodeJsonWithSerializer")
inline fun <reified T> ByteArray.decodeJson(serializer: KSerializer<T> = serializer()): T =
  decodeJson(decodeToString(), serializer)


inline fun <reified T> KResponse<T>.decodeJson(serializer: KSerializer<T> = serializer()): T =
  TODO()

/*
fun <T> decodeCbor(
  input: InputStream,
  serializer: KSerializer<T>,
): T = cbor.decodeFromByteArray(serializer, input.readBytes())
*/

fun <T> decodeCbor(
  input: ByteArray,
  serializer: KSerializer<T>,
): T = cbor.decodeFromByteArray(serializer, input)

fun <T> encodeCbor(t: T, serializer: KSerializer<T>): ByteArray =
  cbor.encodeToByteArray(serializer, t)

inline fun <reified T> T.encodeCbor(): ByteArray = encodeCbor(this, serializer())


@JvmName("decodeCborByteArray")
inline fun <reified T> ByteArray.decodeCbor(): T =
  decodeCbor(this, serializer())

@JvmName("decodeCborByteArrayWithSerializer")
fun <T> ByteArray.decodeCbor(serializer: KSerializer<T>): T =
  decodeCbor(this, serializer)

/*
@JvmName("decodeCborByteInputStream")
inline fun <reified T> InputStream.decodeCbor(): T =
  decodeCbor(readBytes(), serializer())

@JvmName("decodeCborByteInputStreamWithSerializer")
fun <T> InputStream.decodeCbor(serializer: KSerializer<T>): T =
  decodeCbor(readBytes(), serializer)*/
