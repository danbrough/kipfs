#!/bin/bash

cd $(dirname $0)
[ -s "src" ] && cd src


export ANDROID_NDK_HOME=/home/kipfs/src/ndk
ORIGINAL_PATH=$PATH
export PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$ORIGINAL_PATH
#export CROSS_SYSROOT=$KONAN_DATA_DIR/dependencies/target-sysroot-1-android_ndk

./Configure android-arm64 no-shared -D__ANDROID_API__=23 --prefix=$(realpath ../libs/androidArm64)

sleep 2
make install_sw



