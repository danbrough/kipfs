#!/bin/bash

cd `dirname $0` && cd ..

DIR=`pwd`

[ ! -d "demo/libs" ] && mkdir -p demo/libs
cd demo/libs

PATH=$KONAN_DATA_DIR/kotlin-native-prebuilt-linux-x86_64-1.6.10/bin:$PATH
PATH=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin:$PATH
export PATH=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin:$PATH

export CC=x86_64-unknown-linux-gnu-gcc

JAVA_HOME=$DIR/jdk/linuxAmd64

cinterop  -verbose -def $DIR/golib/src/nativeInterop/cinterop/golib.def  -pkg golib2 \
  -libraryPath $DIR/build/native/linuxAmd64 \
  -compiler-options "-I$JAVA_HOME/include -I$JAVA_HOME/include/linux -I$DIR/build/native/linuxAmd64/" \
   -o golib2



