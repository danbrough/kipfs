#!/bin/bash

cd $(dirname $0) && cd ..
export LD_LIBRARY_PATH=$(realpath golib/build/native/linuxAmd64)

if [ -z "$1" ]; then
  IPFS_ADDR=/ip4/127.0.0.1/tcp/5001
else
  IPFS_ADDR=$1
fi

exec ./gradlew demos:native_cmdline:runKipfsDemoDebugExecutableLinuxAmd64 -Pargs=$IPFS_ADDR
