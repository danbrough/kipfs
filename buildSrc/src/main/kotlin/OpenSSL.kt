import org.gradle.api.Project

object OpenSSL {
  
  fun Project.opensslPrefix(platform: PlatformNative<*>) =
    rootProject.file("openssl/lib/${platform.name}")
  
}