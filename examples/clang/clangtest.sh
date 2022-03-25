#!/bin/bash

cd $(dirname $0)


CC=$KONAN_DATA_DIR/dependencies/llvm-11.1.0-linux-x64-essentials/bin/clang
MING=$KONAN_DATA_DIR/dependencies/msys2-mingw-w64-x86_64-1

#$CC -target x86_64-pc-windows-gnu  --sysroot=$MING -L$MING/lib/  -L $MING/lib/gcc/x86_64-w64-mingw32/9.2.0/ test.c -o test.exe


$CC -target x86_64-pc-windows-gnu \
 --sysroot=$MING/x86_64-w64-mingw32/ \
 -L$MING/x86_64-w64-mingw32/lib/  -L$MING/lib/ -L $MING/lib/gcc/x86_64-w64-mingw32/9.2.0/ \
 test.c -o test.exe



