#!/bin/bash

cd $(dirname $0) && cd ..
export SRCDIR=$PWD
echo
echo '##########' running `realpath $0`

export PLATFORM="linuxAmd64"
LIBSDIR=$SRCDIR/build/native/$PLATFORM/static
LIBNAME=$LIBSDIR/libkipfs.a
#OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
#export PKG_CONFIG_PATH=$SRCDIR/openssl/libs/$PLATFORM/lib/pkgconfig/
#export CGO_LDFLAGS="-L$OPENSSL/lib  `pkg-config --libs --shared libssl`  "
export CGO_LDFLAGS="-fPIC"


SYSROOT=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot
PATH=$KONAN_DATA_DIR/dependencies/llvm-11.1.0-linux-x64-essentials/bin:$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin:$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin:$PATH

CROSS_PREFIX=x86_64-unknown-linux-gnu-
#export CC=x86_64-unknown-linux-gnu-gcc
#export CXX=x86_64-unknown-linux-gnu-g++

export CC=clang
export CXX=clang++
export CGO_CC=$CC
export CGO_CXX=$CXX


#export CGO_CFLAGS=" -I$SRCDIR/build/native/linuxAmd64/static/ `pkg-config --cflags libssl`   -I$OPENSSL/include -fPIC "
export CGO_CFLAGS="--sysroot=$SYSROOT -fpic -I$SRCDIR/build/native/linuxAmd64/static/ "
export CGO_CXXFLAGS="$CGO_CFLAGS"
export CGO_LDFLAGS="--sysroot=$SYSROOT -L$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/lib \
-L$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/lib/gcc/x86_64-unknown-linux-gnu/8.3.0/ \
-fpic -L$SRCDIR/build/native/linuxAmd64/static"
echo CGO_LDFLAGS $CGO_LDFLAGS
echo CGO_CFLAGS $CGO_CFLAGS

cd go
#CMD="go build -tags=$TAGS  -v -buildmode=c-archive -o $LIBNAME  ./libs/ "
[ ! -d $LIBSDIR ] && mkdir -p $LIBSDIR
go tool cgo -exportheader $LIBSDIR/libkipfs.h libs/libkshell.go || exit 0

CMD="go build -v -tags=shell,node  -buildmode=c-archive -o $LIBNAME  ./libs/ "
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
