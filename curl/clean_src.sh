#!/bin/bash 

cd `dirname $0` && cd src

if [ ! -d $CURL_SRC ]; then
   echo getting curl src ...
   git clone https://github.com/curl/curl.git $CURL_SRC
fi

echo restoring src to $CURL_VERSION
git clean -xdf > /dev/null 2>&1
git reset --hard  > /dev/null 2>&1
git checkout $CURL_VERSION  > /dev/null 2>&1
autoreconf -fi  > /dev/null

