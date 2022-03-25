#!/bin/bash 

cd $(dirname $0) && cd .. 

exec ./gradlew golib:publishAllPublicationsToMavenRepository $@
