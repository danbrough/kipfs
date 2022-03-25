#!/bin/bash

cd $(dirname $0) && cd ..
export SRCDIR=$PWD
echo '### ' Running $0

[ ! -z "$1" ] && export PLATFORM="$1"
source env.sh

LIBSDIR=$SRCDIR/build/native/$PLATFORM/static
LIBNAME=$LIBSDIR/libkipfs.a
OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
#SYSROOT=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot

case $PLATFORM in
"linuxAmd64")
  echo configuring for linuxAmd64
  SYSROOT=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot
  CROSS_PREFIX=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-
  #export CC=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-gcc
  ;;

"linuxArm")
  echo configuring for linuxArm
  SYSROOT=$KONAN_DATA_DIR/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot
  CROSS_PREFIX=$KONAN_DATA_DIR/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/arm-unknown-linux-gnueabihf-
  export CC=$KONAN_DATA_DIR/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/arm-unknown-linux-gnueabihf-cc
  ;;

"linuxArm64")
  echo configuring for linuxArm64
  SYSROOT=$KONAN_DATA_DIR/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot
  CROSS_PREFIX=$KONAN_DATA_DIR/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-
  export CC=$KONAN_DATA_DIR/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-gcc
  ;;

esac
#export PKG_CONFIG_PATH=$SRCDIR/openssl/libs/$PLATFORM/lib/pkgconfig/
#export CGO_LDFLAGS="-L$OPENSSL/lib  `pkg-config --libs --shared libssl`  "

#export CGO_CFLAGS=" -I$SRCDIR/build/native/linuxAmd64/static/ `pkg-config --cflags libssl`   -I$OPENSSL/include -fPIC "
if [ "$GOOS" == "linux" ]; then
  CGO_CFLAGS="--sysroot=$SYSROOT -I$SRCDIR/build/native/$PLATFORM/static/ -fPIC -pthread "
  export CGO_LDFLAGS=" --sysroot=$SYSROOT -L$OPENSSL/lib $OPENSSL/lib/libssl.a $OPENSSL/lib/libcrypto.a   -ldl -lpthread  "
else
  export PKG_CONFIG_PATH=$SRCDIR/openssl/libs/$PLATFORM/lib/pkgconfig/
  export CGO_CFLAGS="-Wno-deprecated-declarations -fPIC -pthread `pkg-config --cflags libssl ` "
  export CGO_CFLAGS="-pthread  -Wno-deprecated-declarations -fPIC -pthread -I/home/dan/workspace/demos/mpp/openssl/libs/windowsAmd64/include -I/usr/x86_64-w64-mingw32/include   "
  export CGO_LDFLAGS="-lpthread -L/usr/x86_64-w64-mingw32/lib/ -L/home/dan/workspace/demos/mpp/openssl/libs/windowsAmd64/lib -l:libssl.a -l:libcrypto.a -lws2_32 -lgdi32 -lcrypt32"
fi


#PATH=$KONAN_DATA_DIR/kotlin-native-prebuilt-linux-x86_64-1.6.10/bin:$PATH
#export PATH=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin:$PATH
#PATH=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin:$PATH
#export PATH=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin:$PATH
./openssl/build.sh $PLATFORM


echo "CONFIGURATION:"
echo CC $CC $CGO_CC
echo CGO_LDFLAGS $CGO_LDFLAGS
echo CGO_CFLAGS $CGO_CFLAGS
echo CROSS_PREFIX $CROSS_PREFIX
echo SYSROOT $SYSROOT
echo GOOS:$GOOS GOARCH:$GOARCH GOARM:$GOARM



#export GGO_CC=/mnt/files2/cache/konan/dependencies/llvm-11.1.0-linux-x64-essentials/bin/clangw

cd go
#CMD="go build -tags=$TAGS  -v -buildmode=c-archive -o $LIBNAME  ./libs/ "
[ ! -d $LIBSDIR ] && mkdir -p $LIBSDIR
#go tool cgo -exportheader $LIBSDIR/libkipfs.h libs/libkshell.go || exit 0

CMD="go build -v -tags=shell,node,openssl   -buildmode=c-archive -o $LIBNAME  ./libs/ "
echo running $CMD
$CMD
