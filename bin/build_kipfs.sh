#!/bin/bash

cd $(dirname $0) && cd ..
export SRCDIR=$PWD
[ ! -z "$1" ] && export PLATFORM="$1"

./openssl/build.sh

echo
echo '##########' KIPFS build `realpath $0`  $PLATFORM
source env.sh

#export CC=${CROSS_PREFIX}gcc

LIBSDIR=$SRCDIR/golib/build/native/$PLATFORM
LIBNAME=$LIBSDIR/libgokipfs.a
export CGO_CFLAGS="$CFLAGS"
#BUILDMODE=c-archive
#
BUILDMODE=c-shared
LIBNAME=$LIBSDIR/libgokipfs.so


if [ "$GOOS" == "android" ]; then
  BUILDMODE=c-shared
  LIBNAME=$LIBSDIR/libgokipfs.so
fi

export CGO_ENABLED=1
export OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
export PKG_CONFIG_PATH=$OPENSSL/lib/pkgconfig


case "$GOOS" in
"windows")
export CGO_CFLAGS="-fPIC -I$SRCDIR/build/native/$PLATFORM -I$OPENSSL/include $CGO_CFLAGS"
export CGO_LDFLAGS="-fPIC -L$OPENSSL/lib"
;;
"linux")
export CGO_LDFLAGS="$CGO_LDFLAGS -ldl"
#export CGO_CFLAGS="--sysroot=$SYSROOT -fPIC -I$SYSROOT/usr/include -I$SRCDIR/build/native/$PLATFORM -I$OPENSSL/include $CGO_CFLAGS"
#export CGO_LDFLAGS="--sysroot=$SYSROOT -fPIC -L$SYSROOT/lib -L$SYSROOT/usr/lib -L$OPENSSL/lib"
;;
"android")
export CGO_LDFLAGS="$CGO_LDFLAGS -ldl"
#export CGO_CFLAGS="--sysroot=$SYSROOT -fPIC -I$SYSROOT/usr/include -I$SRCDIR/build/native/$PLATFORM -I$OPENSSL/include $CGO_CFLAGS"
#export CGO_LDFLAGS="--sysroot=$SYSROOT -fPIC -L$SYSROOT/lib -L$SYSROOT/usr/lib -L$OPENSSL/lib"
;;
esac



echo
echo '#### CONF:'
echo GOOS:$GOOS GOARCH:$GOARCH GOARM:$GOARM
echo CC:$CC CXX:$CXX CGO_CC:$CGO_CC CC_FOR_TARGET:$CC_FOR_TARGET
echo CGO_CFLAGS:$CGO_CFLAGS
echo CGO_LDFLAGS:$CGO_LDFLAGS
echo BUILDMODE $BUILDMODE
sleep 1

[ ! -d $LIBSDIR ] && mkdir -p $LIBSDIR
cd go

#go tool cgo -exportheader $LIBSDIR/libkipfs.h libs/libkshell.go libs/libknode.go || exit 0


#go build -x -v -tags=shell,node   -ldflags '-linkmode external -extldflags "-static"'  -buildmode=c-archive -o $LIBNAME  ./libs/




CMD="go build -trimpath -v -tags=shell,node,openssl   -buildmode=$BUILDMODE -o $LIBNAME  ./libs/libkshell.go ./libs/main.go"
echo running $CMD
$CMD


#-ldflags="-extld=$CC"
#-ldflags '-linkmode external -extldflags "-static"'

