#!/bin/bash

TEST_PACKAGE=danbroid.kipfs.demo.test

cd $(dirname $0) && cd ..

if [ ! -z "$1" ]; then
  ./gradlew androiddemo:installDebugAndroidTest
  adb shell am instrument -w -r -e debug false -e class $1 \
    $TEST_PACKAGE/androidx.test.runner.AndroidJUnitRunner
else
  ./gradlew androiddemo:connectedDebugAndroidTest

fi
