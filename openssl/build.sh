#!/bin/bash

cd "$(dirname "$0")"

TARGET=android-x86_64
API=23
export ANDROID_API=$API
export CFLAGS="-O3  -Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1"
export PREFIX="/home/dan/workspace/kipfs/openssl/lib/androidNativeX64"
export PATH=/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin

#export ANDROID_NDK_HOME="$(realpath ~/.konan/dependencies/target-toolchain-2-linux-android_ndk)"
#export CROSS_SYSROOT="$KONAN_DATA_DIR/dependencies/target-sysroot-1-android_ndk/android-$API/arch-$ARCH"


function configure_ndk() {
  export ANDROID_NDK_HOME=/mnt/files/sdk/android/ndk/25.0.8775105
  export PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH
}


function configure_kotlin() {
  #export ANDROID_NDK_HOME=/mnt/files/sdk/android/ndk/25.0.8775105
  export ANDROID_NDK_HOME=$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk
  export PATH=$ANDROID_NDK_HOME/bin:$PATH
}

#configure_ndk
configure_kotlin

cd /home/dan/workspace/kipfs/openssl/src/openssl/
( git reset --hard && git clean -xdf ) > /dev/null 2>&1

./Configure   $TARGET no-tests -D__ANDROID_API__=$API --prefix=$PREFIX > configure.log 2>&1 || exit 1
#./Configure   $TARGET no-tests no-stdio -D__ANDROID_API__=$API --prefix=$PREFIX > configure.log 2>&1 || exit 1
make  -j5 | tee build.log
make install_sw -j5



exit 0

PREFIX="$(realpath lib/androidNativeX86)"
cd src/openssl/
#git reset --hard && git clean -xdf

./Configure   $TARGET no-tests no-stdio -D__ANDROID_API__=$API --prefix=$PREFIX
sed -i Makefile -e 's|^AR=.*|AR=llvm-ar|g' -e 's|^RANLIB=.*|RANLIB=ranlib|g'

make install_sw -j5

