#!/bin/bash

cd $(dirname $0) && cd src

export ANDROID_NDK_HOME=/opt/ndk
ORIGINAL_PATH=$PATH
export PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$ORIGINAL_PATH
export CFLAGS="-Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1"

./Configure android-x86 no-shared -D__ANDROID_API__=23 --prefix=$(realpath ../libs/android386)

sleep 2
make install_sw
