package danbroid.demo

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking


object KtorTest {
  val log = danbroid.logging.configure("TEST", coloured = true)

  @kotlin.test.Test
  fun test1() {
    runBlocking {
      log.info("test1()")
      val client = HttpClient(Curl)
      log.debug("created client: $client")

      val response = client.get("https://home.danbrough.org")
      log.debug("got response: ${response.status}")
      response.bodyAsText().also {
        log.info(it)
      }
    }

  }
}