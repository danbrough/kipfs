#!/bin/bash


cd `dirname $0` && cd ..

./scripts/publishAll.sh || exit 1

VERSION=$(./gradlew -q versionName)

echo creating release with version $VERSION
sleep 5


echo commiting version $VERSION

git add .
git commit -am "release: $VERSION"
git tag $VERSION || exit 1
git push
git push origin $VERSION

syncmaven 2> /dev/null

./gradlew -q versionIncrement




