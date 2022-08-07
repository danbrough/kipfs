#!/bin/bash

cd $(dirname "$0")

LIBDIR=../../build/lib/linuxX64/

export LD_LIBRARY_PATH="$LIBDIR"

gcc -o test test.c -lkipfsgo -L $LIBDIR -I $LIBDIR -I ../../src/go/libs/ || exit 1
./test