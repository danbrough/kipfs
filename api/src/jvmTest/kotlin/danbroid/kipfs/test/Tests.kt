package danbroid.kipfs.test

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

val log = danbroid.logging.configure("TEST", coloured = true)

class Tests {
  @Test
  fun test1() {
    log.info("test1()")
    val client = HttpClient(CIO)
    runBlocking {
      val response: HttpResponse = client.get("https://h1.danbrough.org/maven")

      response.receive<String>().also {
        log.debug("read: $it")
      }
    }
  }
}