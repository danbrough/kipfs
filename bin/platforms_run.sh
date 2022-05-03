#!/bin/bash


for platform in $PLATFORMS; do
  export PLATFORM=$platform
  echo PLATFORM is $platform
  $@ || exit 1
done
