[ -z "$SRCDIR" ] && echo SRCDIR NOT SET && exit 1 #set it to the root of the source folder
PS1="\[\033[01;34m\]\u@\h\[\033[01;33m\] \w \$\[\033[00m\] "

PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
[ -d /src/.local ] && PATH=/src/.local/bin:$PATH
export PLATFORM_LINUX_AMD64="linuxAmd64"
export PLATFORM_LINUX_386="linux386"
export PLATFORM_LINUX_ARM="linuxArm"
export PLATFORM_LINUX_ARM64="linuxArm64"
export PLATFORM_WINDOWS_AMD64="windowsAmd64"
export PLATFORM_WINDOWS_386="windows386"
export PLATFORM_ANDROID_386="android386"
export PLATFORM_ANDROID_AMD64="androidAmd64"
export PLATFORM_ANDROID_ARM="androidArm"
export PLATFORM_ANDROID_ARM64="androidArm64"

export CGO_ENABLED=1
export MAKE="make -j5"

export CURL_VERSION=curl-7_82_0
export CURL_LIBS=$SRCDIR/curl/libs/$PLATFORM

export OPENSSL=$SRCDIR/openssl/libs/$PLATFORM
export OPENSSL_TAG=kipfs #OpenSSL_1_1_1n
export PKG_CONFIG_PATH=$OPENSSL/lib/pkgconfig
export PLATFORMS="$PLATFORM_LINUX_AMD64  $PLATFORM_ANDROID_AMD64 $PLATFORM_ANDROID_386  $PLATFORM_ANDROID_ARM $PLATFORM_ANDROID_ARM64$PLATFORM_PLATFORM_LINUX_386 $PLATFORM_LINUX_ARM $PLATFORM_LINUX_ARM64"

function dir_path() {
  find ${@:2} -type d -name "$1" | tr '\n' ':' | sed -e 's/:$//g'
}

[ -z "$GOROOT" ] && export GOROOT=/opt/go

PATH=$GOROOT/bin:$PATH

if [ -z "$CACHEDIR" ]; then
  # a place to store build cache files
  export CACHEDIR=$HOME/.cache
fi

[ -z "KONAN_DATA_DIR" ] && export KONAN_DATA_DIR=$CACHEDIR/konan
export KONAN_DATA_DIR=$CACHEDIR/konan
export ANDROID_NDK_HOME=$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk

#if [ -z "$ANDROID_NDK_ROOT" ]; then
#  export ANDROID_NDK_ROOT=/opt/ndk
#fi

#export ANDROID_NDK_ROOT=$ANDROID_NDK_ROOT

#if [ -z "$ANDROID_HOME" ]; then
#  export ANDROID_HOME=/usr/lib/android-sdk
#fi

#[ -z "$ANDROID_HOME" ] && export ANDROID_HOME=/opt/sdk/android
#[ -z "$ANDROID_NDK_ROOT" ] && export ANDROID_NDK_ROOT=$ANDROID_HOME/ndk/23.1.7779620
#
#export ANDROID_NDK_HOME=$ANDROID_NDK_ROOT
#export PATH=$ANDROID_HOME/platform-tools:$PATH

if [ -z "$PLATFORM" ]; then
  ARCH=$(uname -m)
  if [ "$ARCH" == "x86_64" ]; then
    ARCH=amd64
    export PLATFORM=linuxAmd64
  elif [ "$ARCH" == "aarch64" ]; then
    export PLATFORM=linuxArm64
  elif [ "$ARCH" == "armv7l" ]; then
    export PLATFORM=linuxArm
  fi
fi

export GOARM=7
export GOOS=linux
export LIBNAME="libkipfs.so"
export LIBDIR="$PLATFORM"
export CFLAGS="-O3"
export DEFAULT_ANDROID_API=23
export CC=clang
export CXX=clang++
unset CPP SYSROOT TOOLCHAIN ANDROID_API AR RANLIB

function configure_clang() {
  PATH=$(dir_path bin $KONAN_DATA_DIR/dependencies/llvm-11.1.0-linux-x64-essentials "$TOOLCHAIN"):$PATH
  [ -z "$SYSROOT" ] && export SYSROOT=$(dir_path sysroot "$TOOLCHAIN")
  #export SYSROOT="/src/.cache/konan/dependencies/target-sysroot-1-android_ndk/android-21/arch-arm64"

  export CC="clang --target=$HOST${ANDROID_API} --gcc-toolchain=$TOOLCHAIN  --sysroot=$SYSROOT"
  export CXX="clang++ --target=$HOST${ANDROID_API} --gcc-toolchain=$TOOLCHAIN --sysroot=$SYSROOT"

}

case "$PLATFORM" in
$PLATFORM_LINUX_AMD64)
  export HOST=x86_64-unknown-linux-gnu
  export GOARCH=amd64
  export OPENSSL_PLATFORM=linux-x86_64
  export TOOLCHAIN=$KONAN_DATA_DIR/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2
  configure_clang
  ;;

$PLATFORM_LINUX_386)
  export GOARCH=386
  export OPENSSL_PLATFORM=linux-x86
  ;;

$PLATFORM_LINUX_ARM)
  export HOST=arm-unknown-linux-gnueabihf
  export GOARCH=arm
  export GOARM=7
  export OPENSSL_PLATFORM=linux-armv4
  export CFLAGS="$CFLAGS -mfloat-abi=hard -mcpu=cortex-a53"
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2"
  configure_clang


  #export CC=${CROSS_PREFIX}gcc
  ;;

$PLATFORM_LINUX_ARM64)
  echo '########################################'
  export HOST=aarch64-unknown-linux-gnu
  export GOARCH=arm64
  export GOARM=7
  export OPENSSL_PLATFORM=linux-aarch64
  export CFLAGS="$CFLAGS -mcpu=cortex-a72"
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2"
  configure_clang
  ;;

$PLATFORM_WINDOWS_AMD64)
  #export CC=x86_64-w64-mingw32-gcc
  export HOST=x86_64-w64-mingw32
  export GOOS=windows
  export CFLAGS="$CFLAGS -pthread"
  #export WINDRES=winres
  export RC=windres
  export GOARCH=amd64
  export OPENSSL_PLATFORM=mingw64
  export LIBNAME="libkipfs.dll"
  #export PATH=/usr/x86_64-w64-mingw32/bin:$PATH
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/msys2-mingw-w64-x86_64-1"
  export SYSROOT="$TOOLCHAIN/x86_64-w64-mingw32"
  export TARGET=$HOST${ANDROID_API}
  #export PATH=$(dir_path bin $TOOLCHAIN):$PATH
  configure_clang


  ;;

$PLATFORM_WINDOWS_386)
  export WINDRES=i686-w64-mingw32-windres
  export CC=i686-w64-mingw32-gcc
  export GOOS=windows
  export OPENSSL_PLATFORM=mingw
  export LIBNAME="libkipfs.dll"
  export GOARCH=386
  ;;

$PLATFORM_ANDROID_ARM64)
  export HOST=aarch64-linux-android
  export ANDROID_API=$DEFAULT_ANDROID_API
  export OPENSSL_PLATFORM=android-arm64
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk"
  export RANLIB=aarch64-linux-android-ranlib
  export GOOS=android
  export GOARCH=arm64
  export GOARM=7
  export ANDROID_LIB_NAME=arm64-v8a
  configure_clang

  ;;

\
  $PLATFORM_ANDROID_ARM)
  export OPENSSL_PLATFORM=android-arm
  export HOST=armv7a-linux-androideabi
  export ANDROID_API=$DEFAULT_ANDROID_API
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk"
  export GOOS=android
  export GOARCH=arm
  export GOARM=7
  export ANDROID_LIB_NAME=armeabi-v7a
  configure_clang
  #export LIBDIR=arm64-v8a  armeabi-v7a  x86  x86_64
  #export LIBDIR=android/arm64-v8a
  ;;

$PLATFORM_ANDROID_386)
  export OPENSSL_PLATFORM=android-x86
  export ANDROID_API=$DEFAULT_ANDROID_API
  export HOST=i686-linux-android
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk"
  export GOOS=android
  export GOARCH=386
  export ANDROID_LIB_NAME=x86
  configure_clang
  ;;

$PLATFORM_ANDROID_AMD64)
  export OPENSSL_PLATFORM=android-x86_64
  export HOST=x86_64-linux-android
  export ANDROID_API=$DEFAULT_ANDROID_API
  export TOOLCHAIN="$KONAN_DATA_DIR/dependencies/target-toolchain-2-linux-android_ndk"
  export GOOS=android
  export GOARCH=amd64
  export ANDROID_LIB_NAME=x86_64
  configure_clang
  ;;

*)
  echo invalid platform $PLATFORM && exit 1
  ;;

esac

#if [ "$GOOS" == "android" ]; then
#export PATH=$ANDROID_NDK_ROOT/bin:$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH
#export SYSROOT=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/sysroot/
#export CC=${CROSS_PREFIX}clang
#export CXX=${CROSS_PREFIX}clang++
#fi

#android-arm
#android-arm64 android-armeabi android-mips android-mips64 android-x86
#android-x86_64 android64 android64-aarch64 android64-mips64 android64-x86_64

[ -z "$GRADLE_USER_HOME" ] && export GRADLE_USER_HOME=$CACHEDIR/gradle/

GOCACHEDIR=$CACHEDIR/go
export GOCACHE=$GOCACHEDIR/$PLATFORM/gobuild
export GOPATH=$GOCACHEDIR/$PLATFORM
export GOBIN=$GOCACHEDIR/$PLATFORM/bin
export GOMODCACHE=$GOCACHEDIR/mod

if [ "$GOOS" == "android" ]; then
  PATH=$(dir_path bin $ANDROID_NDK_HOME):$PATH
fi

export PATH=$GOBIN:$PATH

#if [ -z "$GOMODCACHE" ]; then
#	export GOMODCACHE=$GOPATH/$PLATFORM/mod
#fi

if [ -z $JAVA_HOME ]; then
  export JAVA_HOME=$(realpath $(dirname $(realpath /usr/bin/java))/..)
fi

#function install_gobind() {
#  which gobind > /dev/null  && return
#  echo '# installing gobind ...'
#  cd $SRCDIR
#  go mod download
#  go get -d  github.com/danbrough/mobile
#  go install  github.com/danbrough/mobile/cmd/gobind@latest
#}
function git_save() {
  if [ $# -eq 0 ]; then
    COMMENT="update"
  else
    COMMENT="$@"
  fi

  echo saving with comment $COMMENT
  git commit -am "$COMMENT" || exit 1

  REMOTE=$(git remote)

  if [ ! -z "$REMOTE" ]; then
    git push
  fi
}
