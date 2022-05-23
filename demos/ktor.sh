#!/bin/bash

cd $(dirname $0) && cd ..

./gradlew demos:ktor:runKtorDemoDebugExecutableLinuxAmd64
