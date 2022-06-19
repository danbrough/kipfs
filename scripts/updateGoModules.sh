#!/bin/bash

cd $(dirname $0) && cd ../golib/src/go
go get -u ./...
go mod tidy
