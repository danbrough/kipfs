#!/bin/bash

cd $(dirname $0)
export SRCDIR=$(realpath ..)
[ ! -z "$1" ] && export PLATFORM="$1"
source ../env.sh

#./clean_src.sh

#export NDK=/opt/sdk/android/ndk/23.1.7779620

#export AR=/src/.cache/konan/dependencies/llvm-11.1.0-linux-x64-essentials/bin/llvm-ar

#export CROSS_PREFIX=${HOST}-
#export HOST_TAG=linux-x86_64
#export TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$HOST_TAG
#export PATH=$TOOLCHAIN/bin:$PATH
#export AR=$TOOLCHAIN/bin/${CROSS_PREFIX}ar
#export AS=$TOOLCHAIN/bin/${CROSS_PREFIX}as
#export CC=$TOOLCHAIN/bin/${CROSS_PREFIX}clang
#export CXX=$TOOLCHAIN/bin/${CROSS_PREFIX}clang++
#export LD=$TOOLCHAIN/bin/${CROSS_PREFIX}ld
#export RANLIB=$TOOLCHAIN/bin/${CROSS_PREFIX}ranlib
#export STRIP=$TOOLCHAIN/bin/${CROSS_PREFIX}strip
#export CPPFLAGS="$CFLAGS -I$OPENSSL/include"
#export LDFLAGS="-L$OPENSSL/lib"

#TOOLCHAIN=$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk
#PATH=$PATH:$TOOLCHAIN/bin

cd $CURL_SRC
CROSS_PREFIX=$HOST

#export LDFLAGS="-L$OPENSSL/lib -static -l:libcrypto.a -l:libssl.a"
#export CFLAGS="$CFLAGS -I$OPENSSL/include"
#unset PKG_CONFIG_PATH
export LIBS="-ldl"
export CPPFLAGS="$CFLAGS"
echo CURL_VERSION $CURL_VERSION
echo CC $CC
echo CXX $CXX
echo LD $LD
echo AR $AR
echo AS $AS
echo CFLAGS $CFLAGS
echo LDFLAGS $LDFLAGS
echo CURL_DIR $CURL_DIR
echo HOST $HOST
echo RANLIB $RANLIB
echo SYSROOT $SYSROOT
echo OPENSSL $OPENSSL PKG_CONFIG_PATH $PKG_CONFIG_PATH
echo
sleep 1





do_configure(){
  ./configure --host=$HOST --target=$HOST  --with-openssl  --prefix=$CURL_DIR \
  --with-pic --disable-shared --enable-static --enable-libgcc --disable-dependency-tracking \
  --disable-ftp --disable-gopher --disable-file --disable-imap --disable-ldap --disable-ldaps \
  --disable-pop3 --disable-proxy --disable-rtsp --disable-smb --disable-smtp --disable-telnet --disable-tftp \
  --without-gnutls --without-librtmp --disable-dict 2>&1 | tee configure.log
}


do_configure


make 2>&1 | tee build.log
make install
