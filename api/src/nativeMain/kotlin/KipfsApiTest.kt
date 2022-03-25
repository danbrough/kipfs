import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking


val log = danbroid.logging.configure("TEST", coloured = true)

fun main(args: Array<String>) {

  log.info("running main()")
  val url = if (args.isEmpty()) "https://www.google.com" else args[0]

  log.debug("loading url: $url")

  val client = HttpClient(Curl)
  runBlocking {
    val response: HttpResponse = client.get(url)
    response.receive<String>().also {
      log.trace("read: $it")
    }

  }
}