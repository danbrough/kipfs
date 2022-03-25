#!/bin/bash

cd `dirname $0`

LD_LIBRARY_PATH=.

gcc test.c  -o test    -I../build/native/linuxAmd64/static/ \
  -ljnitest  -lssl -lcrypto -lpthread -ldl -L && ./test