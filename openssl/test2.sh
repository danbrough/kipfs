#!/bin/bash

cd $(dirname $0)

[ -s "src" ] && cd src


ORIGINAL_PATH=$PATH

export ANDROID_NDK_HOME=$HOME/.cache/ndk

export PATH=$ANDROID_NDK_HOME/bin:$ORIGINAL_PATH
./Configure android-x86 no-shared -D__ANDROID_API__=23 --prefix=$(realpath ../libs/android386)