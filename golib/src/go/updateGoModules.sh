#!/bin/bash

cd $(dirname $0) && cd kipfs
go get -u ./...
go mod tidy
