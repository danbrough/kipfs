import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*


object JvmDemoLib : DemoLib {

  val log = danbroid.logging.configure("DEMO", coloured = true)
  override fun createHttpClient(): HttpClient = HttpClient(CIO)

}

actual fun initDemoLib(): DemoLib = JvmDemoLib
