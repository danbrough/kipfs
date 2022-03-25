#!/bin/bash

cd $(dirname $0)
export SRCDIR=$(realpath ..)

[ ! -z "$1" ] && export PLATFORM="$1"
source ../env.sh
CURL_VERSION=curl-7_82_0

CURL_SRC=$SRCDIR/curl/src


if [ ! -d $CURL_SRC ]; then
  echo getting curl src ...
  git clone https://github.com/curl/curl.git $CURL_SRC
fi

echo OPENSSL $OPENSSL PKG_CONFIG_PATH $PKG_CONFIG_PATH
export NDK=/opt/sdk/android/ndk/23.1.7779620

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
export CC=$CROSS_PREFIX-gcc
export CPP=$CROSS_PREFIX-cpp
export CXX=$CROSS_PREFIX-c++
export AR=$CROSS_PREFIX-ar
export AS=$CROSS_PREFIX-as
export RANDLIB=$CROSS_PREFIX-randlib
export STRIP=$CROSS_PREFIX-strip
export LD=$CROSS_PREFIX-ld

echo CC $CC
echo CXX $CXX
echo CFLAGS $CFLAGS
echo CURL_DIR $CURL_DIR
echo HOST $HOST
echo SYSROOT $SYSROOT
sleep 1



function clean_src(){
  git clean -xdf
git reset --hard
  git checkout $CURL_VERSION
  autoreconf -fi
}

clean_src

./configure   --host=$HOST --with-openssl --prefix=$CURL_DIR   \
  --with-pic --disable-shared --enable-static --enable-libgcc --disable-dependency-tracking \
   --disable-ftp --disable-gopher --disable-file --disable-imap --disable-ldap --disable-ldaps \
   --disable-pop3 --disable-proxy --disable-rtsp --disable-smb --disable-smtp --disable-telnet --disable-tftp \
   --without-gnutls --without-libidn --without-librtmp --disable-dict 2>&1 | tee configure.log
make 2>&1 | tee build.log
make install
