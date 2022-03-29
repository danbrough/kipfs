#!/bin/bash 

cd $(dirname $0) && cd .. 

exec ./gradlew -Pkipfs.ideMode=false golib:publishAllPublicationsToMavenRepository $@
