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
export CGO_CFLAGS="$CFLAGS"
#BUILDMODE=c-archive
#
BUILDMODE=c-archive
LIBNAME=libgokipfs.so
BUILDMODE=c-shared

#if [ "$GOOS" == "android" ]; then


#fi

export CGO_ENABLED=1
export OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
export PKG_CONFIG_PATH=$OPENSSL/lib/pkgconfig

export CGO_CFLAGS="$CGO_CFLAGS -fPIC"
export CGO_LDFLAGS="$CGO_LDFLAGS -L$OPENSSL/lib -fPIC -ldl"

case "$GOOS" in
"windows")
LIBNAME=libgokipfs.a
BUILDMODE=c-archive
export CGO_CFLAGS=" -I$SRCDIR/build/native/$PLATFORM -I$OPENSSL/include $CGO_CFLAGS"
export CGO_LDFLAGS="-L$OPENSSL/lib -fPIC "

;;
"linux")
export CGO_CFLAGS="$CGO_CFLAGS -fPIC -pthread"
export CGO_LDFLAGS="$CGO_LDFLAGS -L$OPENSSL/lib -fPIC -ldl -lpthread"
#export CGO_LDFLAGS="$CGO_LDFLAGS -fPIC -ldl -lpthread"
#export CGO_CFLAGS="--sysroot=$SYSROOT -fPIC -I$SYSROOT/usr/i nclude -I$SRCDIR/build/native/$PLATFORM -I$OPENSSL/include $CGO_CFLAGS"
#export CGO_LDFLAGS="--sysroot=$SYSROOT -fPIC -L$SYSROOT/lib -L$SYSROOT/usr/lib -L$OPENSSL/lib"
;;
"android")
#export CGO_LDFLAGS="$CGO_LDFLAGS -fPIC  -ldl -lpthread"
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




CMD="go build -trimpath -v -tags=shell,openssl   -buildmode=$BUILDMODE -o $LIBSDIR/$LIBNAME  ./libs/libkshell.go ./libs/main.go"
echo running $CMD
$CMD


#-ldflags="-extld=$CC"
#-ldflags '-linkmode external -extldflags "-static"'

