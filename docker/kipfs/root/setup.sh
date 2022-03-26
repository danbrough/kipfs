#!/bin/bash


GO_VERSION=1.18

case "$(uname -m)" in
"aarch64")
  DOWNLOAD=https://go.dev/dl/go${GO_VERSION}.linux-arm64.tar.gz
  ;;
"x86_64")
  DOWNLOAD=https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz
  apt install -y gcc-arm-linux-gnueabihf gcc-mingw-w64-x86-64-win32
  ;;
"armv7l")
  DOWNLOAD=https://go.dev/dl/go${GO_VERSION}.linux-armv6l.tar.gz
;;
*)
  echo "Unhandled platform $(uname -a)"
  ;;
esac




if [ ! -z "$DOWNLOAD" ]
then
  echo downloading $DOWNLOAD
  wget -q $DOWNLOAD -O - | tar -xvz -C /opt
fi
