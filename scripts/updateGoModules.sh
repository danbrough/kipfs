#!/bin/bash

cd $(dirname $0) && cd ../golib/src/go/kipfs
go get -u ./...
go mod tidy
