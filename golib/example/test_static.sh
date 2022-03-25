#!/bin/bash

cd `dirname $0`
cd ..
../gradlew linkJniDemoDebugStaticNative
cd example

gcc main.c libjnidemo.a -o example || exit 1
./example

