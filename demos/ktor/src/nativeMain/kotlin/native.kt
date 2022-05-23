import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*


object NativeDemoLib : DemoLib {

  val log = danbroid.logging.configure("DEMO", coloured = true)
  override fun createHttpClient(): HttpClient = HttpClient(Curl)

}

actual fun initDemoLib(): DemoLib = NativeDemoLib
