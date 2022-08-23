#!/bin/bash


cd $(dirname $0) && cd ..
source scripts/common.sh

if is_mac; then
  git reset --hard && git pull
  ./gradlew -PpublishDocs publishMacTargetsToSonatypeRepository
  exit 0
fi

set_gradle_prop build.snapshot true

VERSION_NAME=$(./gradlew -q buildVersionName)

./gradlew  publishAllPublicationsToSonatypeRepository
