#!/bin/bash

cd $(dirname $0) && cd ..
export SRCDIR=$PWD



#[ ! -z "$1" ] && export PLATFORM="$1"
PLATFORM=android386
#export ANDROID_NDK_HOME=/opt/ndk

echo
echo '##########' KIPFS build `realpath $0`  $PLATFORM
source env.sh

export CC=$SRCDIR/ndk/toolchains/llvm/prebuilt/linux-x86_64/bin/i686-linux-android23-clang
LIBSDIR=$SRCDIR/build/native/$PLATFORM
LIBNAME=$LIBSDIR/libkipfs.so
export CGO_CFLAGS="$CFLAGS"


export CGO_ENABLED=1
export OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
export PKG_CONFIG_PATH=$OPENSSL/lib/pkgconfig
export CGO_CFLAGS="-fPIC  -I$SRCDIR/build/native/$PLATFORM/static -I$OPENSSL/include $CGO_CFLAGS"
export CGO_LDFLAGS="-fPIC -L$OPENSSL/lib"
#export CC=$SRCDIR/ndk/toolchains/llvm/prebuilt/linux-x86_64/bin/i686-linux-android23-clang


echo
echo '#### CONF:'
echo ANDROID_NDK_HOME $ANDROID_NDK_HOME
echo GOOS:$GOOS GOARCH:$GOARCH GOARM:$GOARM
echo CC:$CC CXX:$CXX CGO_CC:$CGO_CC CC_FOR_TARGET:$CC_FOR_TARGET
echo CGO_CFLAGS:$CGO_CFLAGS
echo CGO_LDFLAGS:$CGO_LDFLAGS

[ ! -d $LIBSDIR ] && mkdir -p $LIBSDIR
cd go

#go tool cgo -exportheader $LIBSDIR/libkipfs.h libs/libkshell.go libs/libknode.go || exit 0


#go build -x -v -tags=shell,node   -ldflags '-linkmode external -extldflags "-static"'  -buildmode=c-archive -o $LIBNAME  ./libs/
# -trimpath
go build  -v -tags=shell,node,openssl   -buildmode=c-shared   -o $LIBNAME  ./libs/

#-ldflags="-extld=$CC"
#-ldflags '-linkmode external -extldflags "-static"'

