#!/bin/bash 

cd $(dirname $0) && cd .. 

exec ./gradlew -DideMode=false golib:publishAllPublicationsToMavenRepository $@
