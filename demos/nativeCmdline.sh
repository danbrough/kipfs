#!/bin/bash

cd $(dirname $0) && cd ..
exec ./gradlew demos:native_cmdline:runKipfsDemoDebugExecutableLinuxAmd64
