import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking


interface DemoLib {
  fun createHttpClient(): HttpClient
}

expect fun initDemoLib(): DemoLib

fun main(args: Array<String>) {
  val demoLib = initDemoLib()
  val log = danbroid.logging.getLog(DemoLib::class)

  runBlocking {
    log.warn("running demo ..")


    val client = demoLib.createHttpClient()
    log.debug("created client: $client")


    val response = client.get("https://home.danbrough.org")
    log.debug("got response: ${response.status}")

    response.bodyAsText().also {
      log.info(it)
    }


    //val selectorManager = ActorSelectorManager(Dispatchers.IO)
  }
}