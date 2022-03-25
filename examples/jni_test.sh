#!/bin/bash

cd `dirname $0`


STATIC_LIBS=`realpath ../build/native/linuxAmd64/static/`


[ -z "$JAVA_HOME" ] && JAVA_HOME=/usr/lib/jvm/default-java/

INCLUDES="-I`pwd` -I$STATIC_LIBS -I$JAVA_HOME/include -I$JAVA_HOME/include/linux"

clang -Wall -Werror -pthread  -fPIC  $INCLUDES  -c jni.c || exit 1

STATIC_LIBS=" $STATIC_LIBS/libkipfs.a "
javac -h . -cp ../golib/build/classes/kotlin/jvm/main/:. GoTest.java golib.java

CMD="clang -v -fuse-ld=gold   -shared -o libkipfs.so   jni.o -fPIC  -L/usr/lib -lcrypto -lssl -ldl -lc  -lpthread  -Wl,-Bsymbolic  $STATIC_LIBS "
#CMD="gcc -shared -o libkipfs.so   jni.o -fPIC  -Wl,-Bsymbolic $STATIC_LIBS -lcrypto -lssl"
echo running $CMD
$CMD || exit 1



echo
echo '###' running jni test

export LD_LIBRARY_PATH=`pwd`
java -cp ../golib/build/classes/kotlin/jvm/main/:. GoTest
gcc -c test.c -I../build/native/linuxAmd64/static/
#gcc test.c -I../build/native/linuxAmd64/static/ -lkipfs -L. || exit 1

gcc -o bin/test_jni  test.o   -lkipfs -L. && ./bin/test_jni  || exit 1







