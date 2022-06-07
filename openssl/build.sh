#!/bin/bash

cd $(dirname $0)
export SRCDIR=$(realpath ..)

[ ! -z "$1" ] && export PLATFORM="$1" && echo set PLATFORM to $PLATFORM from cmd arg


source ../env.sh
../curl/zlib/build.sh 
#export OPENSSL_TAG=kipfs_1.1.1n
export SRC=$SRCDIR/openssl/src

#echo LIBS: $LIBS
#echo SRC: $SRC

#export CROSS_PREFIX=${HOST}-
#export CFLAGS="$CFLAGS -Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1"
export CFLAGS="$CFLAGS -Wno-macro-redefined -Wno-deprecated-declarations "
export LDFLAGS="$LDFLAGS -L$SRCDIR/curl/zlib/libs/$PLATFORM/lib"
if [ ! -d $SRC ]; then
  echo "downloading source .."
  git clone https://github.com/danbrough/openssl.git $SRC || exit 1
  cd $SRC
  git checkout $OPENSSL_TAG  || exit 1
fi

#EXTRAS=" --sysroot=$SYSROOT"
#EXTRAS="enable-md4 enable-des disable-shared"



#export PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH

#export CC=$KONAN_DATA_DIR/dependencies/llvm-11.1.0-linux-x64-essentials/bin/clang

#export PATH=$SRCDIR/openssl/bin:$PATH


function clean_src(){
    cd $SRC
    git clean -xdf >/dev/null
    git reset --hard >/dev/null
    git checkout $OPENSSL_TAG
}

echo OPENSSL is $OPENSSL
CRYPTO_LIB=$OPENSSL/lib/libcrypto.a

export RANLIB=/mnt/files2/cache/konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/bin/ranlib
if [ -f $CRYPTO_LIB ]; then
  echo not building openssl as $CRYPTO_LIB exists
else
  echo OPENSSL_PLATFORM $OPENSSL_PLATFORM
  echo OPENSSL $OPENSSL
  echo CC $CC CXX: $CXX
  echo CFLAGS $CFLAGS
  echo SYSROOT $SYSROOT
  echo CROSS_PREFIX $CROSS_PREFIX
  echo ANDROID_API $ANDROID_API
  echo RANLIB $RANLIB 
  sleep 2

  clean_src
  cd $SRC

  if [ "$GOOS" == "android" ]; then
    ./Configure $OPENSSL_PLATFORM no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$OPENSSL" $EXTRAS || exit 1
  else
    ./Configure --prefix="$OPENSSL" $OPENSSL_PLATFORM   $EXTRAS || exit 1
  fi
  make install_sw || exit 1
fi


#arch name: android version : folder
#for arch in arm64:21:arm64-v8a arm:16:armeabi-v7a x86:16:x86 x86_64:21:x86_64; do
#for arch in x86_64:21:x86_64; do
#  echo compiling $arch
##  cd $SRC
#git clean -xdf
#ARGS=(${arch//:/ })
#ARCH=${ARGS[0]}
#ANDROID_API=${ARGS[1]}
#INSTALLDIR="$LIBS/${ARGS[2]}"
#echo "ARCH $ARCH ANDROID_API: $ANDROID_API INSTALLDIR: $INSTALLDIR"
#[ -d $INSTALLDIR ] && rm -rf $INSTALLDIR
#./Configure android-$ARCH no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$INSTALLDIR" || exit 1
#make || exit 1
#make install_sw || exit 1
#done
