#!/bin/bash


cd `dirname $0` && cd ..

./scripts/publishAll.sh || exit 1

VERSION=$(./gradlew -q versionName)

echo commiting version $VERSION

git add .
git commit -am "release: $VERSION"
git tag $VERSION || exit 1


