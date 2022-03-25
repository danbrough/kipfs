import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

val log = danbroid.logging.configure("TEST", coloured = true)

class Tests {
  @Test
  fun test1() {
    log.info("test1()")
    val client = HttpClient(Curl)
    runBlocking {
      val response: HttpResponse = client.get("https://h1.danbrough.org/maven")
      response.readText().also {
        log.debug("response: $it")
      }
    }
  }
}