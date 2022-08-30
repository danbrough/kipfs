package openssl

import klog.*
import kotlinx.cinterop.*
import kotlin.test.Test
import libopenssl.*

private val log = klog("openssl") {
  messageFormatter = KMessageFormatters.verbose.colored
  level = Level.TRACE
  writer = KLogWriters.stdOut
}

class Tests {
  @Test
  fun test() {
    log.debug("running test()")
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
}

/*
/*
 * Copyright 2013-2021 The OpenSSL Project Authors. All Rights Reserved.
 *
 * Licensed under the OpenSSL license (the "License").  You may not use
 * this file except in compliance with the License.  You can obtain a copy
 * in the file LICENSE in the source distribution or at
 * https://www.openssl.org/source/license.html
 */

#include <string.h>
#include <openssl/err.h>
#include <openssl/ssl.h>

int main(int argc, char **argv)
{
    BIO *sbio = NULL, *out = NULL;
    int len;
    char tmpbuf[1024];
    SSL_CTX *ctx;
    SSL_CONF_CTX *cctx;
    SSL *ssl;
    char **args = argv + 1;
    const char *connect_str = "home.danbrough.org:443";
    int nargs = argc - 1;

    ctx = SSL_CTX_new(TLS_client_method());
    cctx = SSL_CONF_CTX_new();
    SSL_CONF_CTX_set_flags(cctx, SSL_CONF_FLAG_CLIENT);
    SSL_CONF_CTX_set_ssl_ctx(cctx, ctx);
    while (*args && **args == '-') {
        int rv;
        /* Parse standard arguments */
        rv = SSL_CONF_cmd_argv(cctx, &nargs, &args);
        if (rv == -3) {
            fprintf(stderr, "Missing argument for %s\n", *args);
            goto end;
        }
        if (rv < 0) {
            fprintf(stderr, "Error in command %s\n", *args);
            ERR_print_errors_fp(stderr);
            goto end;
        }
        /* If rv > 0 we processed something so proceed to next arg */
        if (rv > 0)
            continue;
        /* Otherwise application specific argument processing */
        if (strcmp(*args, "-connect") == 0) {
            connect_str = args[1];
            if (connect_str == NULL) {
                fprintf(stderr, "Missing -connect argument\n");
                goto end;
            }
            args += 2;
            nargs -= 2;
            continue;
        } else {
            fprintf(stderr, "Unknown argument %s\n", *args);
            goto end;
        }
    }

    if (!SSL_CONF_CTX_finish(cctx)) {
        fprintf(stderr, "Finish error\n");
        ERR_print_errors_fp(stderr);
        goto end;
    }

    /*
     * We'd normally set some stuff like the verify paths and * mode here
     * because as things stand this will connect to * any server whose
     * certificate is signed by any CA.
     */

    sbio = BIO_new_ssl_connect(ctx);

    BIO_get_ssl(sbio, &ssl);

    if (!ssl) {
        fprintf(stderr, "Can't locate SSL pointer\n");
        goto end;
    }

    /* Don't want any retries */
    SSL_set_mode(ssl, SSL_MODE_AUTO_RETRY);

    /* We might want to do other things with ssl here */

    BIO_set_conn_hostname(sbio, connect_str);

    out = BIO_new_fp(stdout, BIO_NOCLOSE);
    if (BIO_do_connect(sbio) <= 0) {
        fprintf(stderr, "Error connecting to server\n");
        ERR_print_errors_fp(stderr);
        goto end;
    }

    /* Could examine ssl here to get connection info */

    BIO_puts(sbio, "GET / HTTP/1.0\n\n");
    for (;;) {
        len = BIO_read(sbio, tmpbuf, 1024);
        if (len <= 0)
            break;
        BIO_write(out, tmpbuf, len);
    }
 end:
    SSL_CONF_CTX_free(cctx);
    BIO_free_all(sbio);
    BIO_free(out);
    return 0;
}

 */