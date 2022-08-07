#!/bin/bash

cd $(dirname "$0")

if [ "$MACH_TYPE" = "x86_64-apple-darwin21" ]; then
  LIBDIR="$(realpath ../../build/lib/macosX64/)"
else
  LIBDIR="$(realpath ../../build/lib/linuxX64/)"
fi

echo "LIBDIR $LIBDIR"

export LD_LIBRARY_PATH="$LIBDIR"

gcc -o test test.c -lkipfsgo -L "$LIBDIR" -I "$LIBDIR" -I ../../src/go/libs/ || exit 1
./test