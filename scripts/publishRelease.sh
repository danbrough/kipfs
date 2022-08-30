#!/bin/bash


cd $(dirname $0) && cd ..
source scripts/common.sh

if is_mac; then
  git reset --hard && git pull
  ./gradlew -PpublishDocs publishMacTargetsToSonatypeRepository
  exit 0
fi

set_gradle_prop build.snapshot false

VERSION_NAME=$(./gradlew -q buildVersionNameNext)

echo Creating release: $VERSION_NAME

if ! message_prompt "Creating release $VERSION_NAME. Continue?"; then
  exit 0
fi
sed -i README.md  -e 's|kipfs:.*"|kipfs:'$VERSION_NAME'"|g'

./gradlew -q buildVersionIncrement
./gradlew publishAllPublicationsToSonatypeRepository

git add .
git commit -am "$VERSION_NAME"

git tag "$VERSION_NAME"
git push
git push origin "$VERSION_NAME"



