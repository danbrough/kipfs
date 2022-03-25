#!/bin/bash

cd $(dirname $0)
unset CLANG CXX CPP CLANGXX LD AR RANLIB

ANDROID_NDK_HOME=/src/.cache/konan/dependencies/target-toolchain-2-linux-android_ndk
#ANDROID_NDK_HOME=/opt/sdk/android/ndk/23.1.7779620
ANDROID_API=23
SRC=/src/openssl/src
#OPENSSL=/src/openssl/libs/android386
#OPENSSL_PLATFORM=android-x86
#OPENSSL=/src/openssl/libs/androidArm64
#OPENSSL_PLATFORM=android-arm64
OPENSSL=/src/openssl/libs/androidArm
OPENSSL_PLATFORM=android-arm
#OPENSSL=/src/openssl/libs/androidAmd64
#OPENSSL_PLATFORM=android-x86_64
OPENSSL_TAG=kipfs
#OPENSSL_TAG=OpenSSL_1_1_1n

#export CROSS_SYSROOT=i686-linux-android-clang
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
export CFLAGS="$CFLAGS -Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1"
function dir_path() {
  find ${@:2} -type d -name "$1" | tr '\n' ':' | sed -e 's/:$//g'
}

PATH=$(dir_path bin $ANDROID_NDK_HOME):$PATH

function clean_src() {
  cd $SRC
  git clean -xdf >/dev/null
  git reset --hard >/dev/null
  git checkout $OPENSSL_TAG
}

clean_src

echo $PATH
cd $SRC
export CC=clang
./Configure $OPENSSL_PLATFORM no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$OPENSSL" || exit 1
sleep 5
make install_sw || exit 1


#arch name: android version : folder
#for arch in arm64:21:arm64-v8a arm:16:armeabi-v7a x86:16:x86 x86_64:21:x86_64; do
#for arch in x86_64:21:x86_64; do
#  echo compiling $arch
##  cd $SRC
#git clean -xdf
#ARGS=(${arch//:/ })
#ARCH=${ARGS[0]}
#ANDROID_API=${ARGS[1]}
#INSTALLDIR="$LIBS/${ARGS[2]}"
#echo "ARCH $ARCH ANDROID_API: $ANDROID_API INSTALLDIR: $INSTALLDIR"
#[ -d $INSTALLDIR ] && rm -rf $INSTALLDIR
#./Configure android-$ARCH no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$INSTALLDIR" || exit 1
#make || exit 1
#make install_sw || exit 1
#done
