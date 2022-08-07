#!/bin/bash

cd $(dirname "$0")

if [ "$MACHTYPE" = "x86_64-apple-darwin21" ]; then
  LIBDIR="$(realpath ../../build/lib/macosX64/)"
  export DYLD_LIBRARY_PATH="$LIBDIR"
else
  LIBDIR="$(realpath ../../build/lib/linuxX64/)"
  export LD_LIBRARY_PATH="$LIBDIR"
fi

echo "LIBDIR $LIBDIR"

gcc -o test test.c -lkipfsgo -L "$LIBDIR" -I "$LIBDIR" -I ../../src/go/libs/ || exit 1
./test