package danbroid.demo

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

val log = danbroid.logging.configure("TEST")

fun main(args: Array<String>) {
  runBlocking {
    log.warn("running demo ..")

    val client = HttpClient(Curl)
    log.debug("created client: $client")

    val response = client.get("https://home.danbrough.org")
    log.debug("got response: ${response.status}")
    response.bodyAsText().also {
      log.info(it)
    }
  }
}