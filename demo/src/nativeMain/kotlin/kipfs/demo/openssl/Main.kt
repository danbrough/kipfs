package kipfs.demo.openssl

import klog.*
import kotlinx.cinterop.*
import kotlin.test.Test
import libopenssl.*

val log = klog("kipfs.demo.openssl") {
  messageFormatter = KMessageFormatters.verbose.colored
  level = Level.TRACE
  writer = KLogWriters.stdOut
}

fun main(){
  log.info("running openSSL demo")
  val connectString = "www.danbrough.org:443"
  log.info("making https request to $connectString")

  val ctx = SSL_CTX_new(TLS_client_method());
  val cctx = SSL_CONF_CTX_new()
  SSL_CONF_CTX_set_flags(cctx, SSL_CONF_FLAG_CLIENT);
  SSL_CONF_CTX_set_flags(cctx, SSL_CONF_FLAG_CLIENT)
  SSL_CONF_CTX_set_ssl_ctx(cctx, ctx)

  val sbio = BIO_new_ssl_connect(ctx)
  //BIO_ctrl(b,BIO_C_GET_SSL,0,(char *)(sslp))
  BIO_ctrl(sbio, BIO_C_GET_SSL, 0, connectString.utf8)

  //SSL_set_mode(ssl, SSL_MODE_AUTO_RETRY); = //SSL_CTX_ctrl((ctx),SSL_CTRL_MODE,(op),NULL)
  SSL_CTX_ctrl(ctx, SSL_CTRL_MODE,SSL_MODE_AUTO_RETRY.toLong(),null)

  //BIO_set_conn_hostname(sbio, connect_str);
  /*#  define BIO_set_conn_hostname(b,name) BIO_ctrl(b,BIO_C_SET_CONNECT,0, \
                                               (char *)(name))*/
  BIO_ctrl(sbio, BIO_C_SET_CONNECT,0,connectString.utf8)


  /*
      out = BIO_new_fp(stdout, BIO_NOCLOSE);
  if (BIO_do_connect(sbio) <= 0) {
      fprintf(stderr, "Error connecting to server\n");
      ERR_print_errors_fp(stderr);
      goto end;
  }
   */



  /*
  #  define BIO_do_connect(b)       BIO_do_handshake(b)
  # define BIO_do_handshake(b)     BIO_ctrl(b,BIO_C_DO_STATE_MACHINE,0,NULL)
   */

  if (BIO_ctrl(sbio, BIO_C_DO_STATE_MACHINE,0,null) <= 0){
    log.error("Failed to connect to server")
    ERR_print_errors_fp(platform.posix.stderr)
    platform.posix.exit(1)
  }



  BIO_puts(sbio, "GET / HTTP/1.0\n\n")


  val buf = ByteArray(1024).toCValues()
  memScoped {
    val ptr = buf.getPointer(this)
    while (true) {
      val n = BIO_read(sbio, ptr, buf.size)
      if (n <= 0) break
      ptr.readBytes(n).also {
        log.warn(it.decodeToString())
      }
    }
  }

  SSL_CONF_CTX_free(cctx);
  BIO_free_all(sbio)
  log.info("finished")
}