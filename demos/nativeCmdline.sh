#!/bin/bash

cd $(dirname $0) && cd ..
export LD_LIBRARY_PATH=$(realpath golib/build/native/linuxAmd64)

exec ./gradlew demos:native_cmdline:runKipfsDemoDebugExecutableLinuxAmd64 $@
