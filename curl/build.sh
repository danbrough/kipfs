#!/bin/bash

cd $(dirname $0)
export SRCDIR=$(realpath ..)
export CURL_SRC=`realpath src`
[ ! -z "$1" ] && export PLATFORM="$1"
source ../env.sh
#./zlib/build.sh
[ -d "$CURL_LIBS" ] && echo not building curl as $CURL_LIBS exists && exit 0

[ "$GOOS" == "android" ] && echo not building for $PLATFORM && exit 0

if [ ! -d $CURL_SRC ]; then
  echo getting curl src ...
  git clone https://github.com/curl/curl.git $CURL_SRC
fi

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

cd $CURL_SRC
#export CC=clang
#export CXX=clang++

export CFLAGS="$CFLAGS -fPIC"
export LDFLAGS="$LDFLAGS -fPIC"

#export PATH=/usr/bin:$PATH
echo OPENSSL $OPENSSL PKG_CONFIG_PATH $PKG_CONFIG_PATH
echo CC $CC CXX $CXX
echo CFLAGS $CFLAGS
echo HOST: $HOST
sleep 2

function clean_src(){
  git clean -xdf
  git reset --hard
  git checkout $CURL_VERSION
  autoreconf -fi
}

clean_src > /dev/null

#./configure --host=$HOST --target=$HOST  --with-openssl --with-zlib=$SRCDIR/curl/zlib/libs/$PLATFORM --prefix=$CURL_LIBS   \
./configure --host=$HOST --target=$HOST  --with-openssl --prefix=$CURL_LIBS   \
  --with-pic --enable-shared --enable-static --disable-libgcc --disable-dependency-tracking \
   --disable-ftp --disable-gopher --disable-file --disable-imap --disable-ldap --disable-ldaps \
   --disable-pop3 --disable-proxy --disable-rtsp --disable-smb --disable-smtp --disable-telnet --disable-tftp \
   --without-gnutls --without-libidn --without-librtmp --disable-dict 2>&1 | tee configure.log
make 2>&1 | tee build.log
make install
