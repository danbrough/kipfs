#!/bin/bash


for platform in $PLATFORMS; do
  export PLATFORM=$platform
  $@ || exit 1
done
