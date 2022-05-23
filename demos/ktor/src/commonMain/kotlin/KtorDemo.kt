
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers

val log = danbroid.logging.configure("KTORDEMO", coloured = true)

expect fun httpClient(): HttpClient

fun main(args: Array<String>) {
  runBlocking {
    log.warn("running demo ..")

    val client = httpClient()
    log.debug("created client: $client")


    val response = client.get("https://home.danbrough.org")
    log.debug("got response: ${response.status}")

    response.bodyAsText().also {
      log.info(it)
    }




    //val selectorManager = ActorSelectorManager(Dispatchers.IO)
  }
}