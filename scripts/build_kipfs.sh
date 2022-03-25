#!/bin/bash

cd $(dirname $0) && cd ..
export SRCDIR=$PWD

export PLATFORM="linuxAmd64"
LIBSDIR=$SRCDIR/build/native/$PLATFORM/static
LIBNAME=$LIBSDIR/libkipfs.a
#OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
#export PKG_CONFIG_PATH=$SRCDIR/openssl/libs/$PLATFORM/lib/pkgconfig/
#export CGO_LDFLAGS="-L$OPENSSL/lib  `pkg-config --libs --shared libssl`  "
export CGO_LDFLAGS="-lssl -lcrypto -fPIC"

#export CGO_CFLAGS=" -I$SRCDIR/build/native/linuxAmd64/static/ `pkg-config --cflags libssl`   -I$OPENSSL/include -fPIC "
export CGO_CFLAGS=" -I$SRCDIR/build/native/linuxAmd64/static/ -pthread  -fPIC "
export CGO_CXXFLAGS="-fPIC"

echo CGO_LDFLAGS $CGO_LDFLAGS
echo CGO_CFLAGS $CGO_CFLAGS

cd go
#CMD="go build -tags=$TAGS  -v -buildmode=c-archive -o $LIBNAME  ./libs/ "
[ ! -d $LIBSDIR ] && mkdir -p $LIBSDIR
go tool cgo -exportheader $LIBSDIR/libkipfs.h libs/libkshell.go || exit 0

CMD="go build -x -v -tags=shell,node,openssl  -buildmode=c-archive -o $LIBNAME  ./libs/ "
echo running $CMD
$CMD

exit 0

ar -M <<EOM
    CREATE $LIBSDIR/libkipfsbig.a
    ADDLIB $LIBSDIR/libkipfs.a
    ADDLIB $OPENSSL/lib/libssl.a
    ADDLIB $OPENSSL/lib/libcrypto.a
    SAVE
    END
EOM
ranlib $LIBSDIR/libkipfsbig.a

#strip $LIBNAME
