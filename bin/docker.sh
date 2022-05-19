#!/bin/bash

cd `dirname $0`

export SRCDIR=$(realpath ..)
if [  -z "$PLATFORM" ]; then
	echo PLATFORM not set
	using default PLATFORM=linux/amd64
	PLATFORM=linux/amd64
fi
echo PLATFORM=$PLATFORM
NAME=kipfs_$(echo $PLATFORM | sed -e 's:\/:_:g')
USER=bob
TMPDIR=/tmp/$NAME
HOMEDIR=/src
DOCKER_IMAGE=docker.io/danbrough/kipfs:latest
[ ! -d $TMPDIR ] && mkdir $TMPDIR

if [ ! -z "$@" ]; then
  DOCKER_COMMAND="$@"
else
  DOCKER_COMMAND=bash
fi


CMD="docker run -it  --tmpfs /tmp \
  --name "${NAME}" \
  --platform=$PLATFORM -h ${NAME} \
	-v $SRCDIR:$HOMEDIR \
	-v $CACHEDIR:$HOMEDIR/.cache \
	-v $CACHEDIR/konan:$HOMEDIR/.konan \
	-v $CACHEDIR/conan:$HOMEDIR/.conan \
	-v $ANDROID_HOME:/opt/sdk/android  \
	--rm -u $USER -w $HOMEDIR \
	$DOCKER_IMAGE  $DOCKER_COMMAND"



echo running $CMD
$CMD
#--rm -u $USER -w /home/$NAME \
#	--mount type=tmpfs,destination=/tmp \

