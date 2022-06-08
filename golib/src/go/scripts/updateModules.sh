#!/bin/bash

cd $(dirname $0) && cd ..
go get -u ./...
go mod tidy
