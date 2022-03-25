#!/bin/bash

cd `dirname $0` && cd ..

export SRCDIR=`pwd`


export PLATFORM=android386
source env.sh
cd tmp
PATH=$KONAN_DATA_DIR/kotlin-native-prebuilt-linux-x86_64-1.6.10/bin:$PATH
cinterop  -verbose -def jni.def  -pkg jni \
  -compiler-options "-I$SYSROOT/usr/include" \
  -target android_x86 \
   -o jni



