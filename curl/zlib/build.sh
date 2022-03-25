#!/bin/bash

export ZLIB_TAG="v1.2.11"

cd `dirname $0`
export SRCDIR=`realpath ../../`
export ZLIB_SRC=`realpath src`
export ZLIB_LIBS=`realpath libs`

[ ! -z "$1" ] && export PLATFORM="$1"
source ../../env.sh

export PREFIX="$ZLIB_LIBS/$PLATFORM"
[ -d "$PREFIX" ] && echo "not building zlib as $PREFIX exists" && exit 0

function clean_src(){
  if [ ! -d "$ZLIB_SRC" ]; then
    git clone https://github.com/madler/zlib.git $ZLIB_SRC
  fi
  cd $ZLIB_SRC
  git clean -xdf
  git reset --hard
  git checkout $ZLIB_TAG
}

clean_src > /dev/null

#export ANDROID_SYSROOT="$KONAN_DATA_DIR/dependencies/target-sysroot-1-android_ndk/android-21/arch-arm64"
#export LDFLAGS="$LDFLAGS -L$ANDROID_SYSROOT/usr/lib"
#export CPPFLAGS="$CPPFLAGS --sysroot=$ANDROID_SYSROOT"
#export CFLAGS="$CFLAGS --sysroot=$ANDROID_SYSROOT"
#export CXXFLAGS="$CXXFLAGS --sysroot=$ANDROID_SYSROOT"

#export CC="clang --target=i686-linux-android21"
#export CC="clang --target=x86_64-linux-android23"
#export RANLIB=$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk/bin/x86_64-linux-android-ranlib
cd $ZLIB_SRC
export RANLIB=${HOST}-ranlib
echo CC $CC HOST: $HOST
echo CXX $CXX
sleep 2


./configure  --static  --prefix=$PREFIX
make -j5 && make install

