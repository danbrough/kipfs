#!/bin/bash

cd `dirname $0` && cd ..
export SRCDIR=$PWD
source env.sh

NAME=kipfs

DOCKER_IMAGE=docker.io/danbrough/debian:latest
#DOCKER_IMAGE=docker.io/danbrough/debian:latest@sha256:42fa0907020ccaa774d04a586ca2fd21de3329c509dbdbf02aa3ebaa6325b8e8
#PLATFORM=$AMD64
#NAME=debby_amd64
#	-v $CACHEDIR:/cache -v /tmp:/tmp -v $CACHEDIR/$PLATFORM:/home/kipfs/.cache \
#	--platform=linux/$PLATFORM  --rm -u kipfs  \
[ ! -d /tmp/kipfs ] && mkdir /tmp/kipfs

docker run -it --name "${NAME}"  --platform=linux/arm/7 -h ${NAME} \
	-v $SRCDIR:/home/kipfs/src \
	-v $CACHEDIR:/home/kipfs/.cache \
	-v /tmp/kipfs:/tmp/  \
	-v $CACHEDIR/konan:/home/kipfs/.konan \
	-v $ANDROID_HOME:/opt/sdk/android  \
	--rm -u kipfs -w /home/kipfs/src \
	$DOCKER_IMAGE  \
	$@
	#/home/kipfs/ipfs_mobile/docker/setup.sh
