#!/bin/bash


cd `dirname $0` && cd ..

./scripts/publishAll.sh || exit 1

VERSION=$(./gradlew -q versionName)

echo commiting version $VERSION


