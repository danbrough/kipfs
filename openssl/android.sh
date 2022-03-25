#!/bin/bash

cd $(dirname $0)
export SRCDIR=$(realpath ..)

[ ! -z "$1" ] && export PLATFORM="$1"
source ../env.sh



#export OPENSSL_TAG=kipfs_1.1.1n
export SRC=$SRCDIR/openssl/src

export ANDROID_API=21
#export OPENSSL_PLATFORM=android-x86
#export OPENSSL=/src/openssl/libs/android386
#export OPENSSL_PLATFORM=android-arm64
#export OPENSSL=/src/openssl/libs/androidArm64
unset CC CPP
#echo LIBS: $LIBS
#echo SRC: $SRC


function dir_path() {
  find ${@:2} -type d -name "$1" | tr '\n' ':' | sed -e 's/:$//g'
}

export CFLAGS="$CFLAGS -Wno-macro-redefined -Wno-deprecated-declarations -DOPENSSL_SMALL_FOOTPRINT=1"
#export AR=/src/.cache/konan/dependencies/llvm-11.1.0-linux-x64-essentials/bin/llvm-ar

if [ ! -d $SRC ]; then
  echo "downloading source .."
  git clone https://github.com/danbrough/openssl.git $SRC || exit 1
  cd $SRC
  git checkout $OPENSSL_TAG || exit 1
fi

#export PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH

#export CC=$KONAN_DATA_DIR/dependencies/llvm-11.1.0-linux-x64-essentials/bin/clang

function clean_src() {
  cd $SRC
  git clean -xdf >/dev/null
  git reset --hard >/dev/null
  git checkout $OPENSSL_TAG
}

export RANLIB=ranlib

ANDROID_CLANG=${HOST}-clang

if [ -d $OPENSSL ]; then
  echo not building openssl as $OPENSSL exists
else
  export CC=clang
  export CXX=clang++
  unset SYSROOT
  export AR=ar
  export ANDROID_API=21
  export TARGET=$HOST
  echo HOST $HOST
  echo OPENSSL_PLATFORM $OPENSSL_PLATFORM
  echo OPENSSL $OPENSSL
  echo CC $CC CXX: $CXX
  echo CFLAGS $CFLAGS
  echo SYSROOT $SYSROOT
  echo ANDROID_API $ANDROID_API
  echo RANLIB $RANLIB
  echo TOOLCHAIN $TOOLCHAIN
  echo CROSS_PREFIX $CROSS_PREFIX
  echo ANDROID_CLANG $ANDROID_CLANG

  sleep 2
  #clean_src



  if ! which $ANDROID_CLANG > /dev/null 2>&1; then
    echo $ANDROID_CLANG not found!
    VERSIONED_CLANG=$(echo ${HOST}${ANDROID_API}-clang | sed -e 's:v7a::g')
    echo VERSIONED_CLANG $VERSIONED_CLANG
    CLANG_PATH=$(which $VERSIONED_CLANG)
    [ -z "$CLANG_PATH" ] && echo $VERSIONED_CLANG not found && exit 1

    #mkdir $SRC/bin 2> /dev/null
    #echo cp $CLANG_PATH "$SRC/bin/$ANDROID_CLANG"
    #cp $CLANG_PATH "$SRC/bin/$ANDROID_CLANG"
    ln -s $CLANG_PATH $ANDROID_NDK_HOME/bin/$ANDROID_CLANG

    if ! which $ANDROID_CLANG > /dev/null 2>&1; then
      echo $ANDROID_CLANG still not found
      exit 1
    fi
  fi


  cd $SRC

  #export ANDROID_NDK_HOME=/opt/sdk/android/ndk/22.1.7171670
  #PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$ANDROID_NDK_HOME/toolchains/x86-4.9/prebuilt/linux-x86_64/bin:$PATH

  echo PATH is $PATH

  #export AR=llvm-ar
  #export ANDROID_NDK_HOME=$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk
  #export PATH=$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk/bin:$PATH

  #export ANDROID_NDK_HOME=/home/whoever/Android/android-sdk/ndk/20.0.5594570
  #PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$ANDROID_NDK_HOME/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin:$PATH
  #./Configure android-arm64 -D__ANDROID_API__=29

  #

  #export ANDROID_NDK_HOME=/home/whoever/Android/android-sdk/ndk/20.0.5594570
  #PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin:$ANDROID_NDK_HOME/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin:$PATH
  #./Configure android-arm64 -D__ANDROID_API__=29

  echo CC finally is $CC

  ./Configure $OPENSSL_PLATFORM no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$OPENSSL" || exit 1

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
