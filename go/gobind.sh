#!/bin/bash
cd $(dirname $0)
rm -rf bind 2> /dev/null
gobind -javapkg danbroid.kipfs -lang go,java -outdir bind -tags shell,openssl ./shell/shell.go

