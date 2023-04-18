package core

import (
	log "github.com/sirupsen/logrus"
	"io"
	"os"
)

type Reader = io.Reader
type Closer = io.Closer
type ReadCloser = io.ReadCloser

func WriteStuff(data []byte, path string) {
	log.Infof("Writing stuff to %s", path)
	err := os.WriteFile(path, data, 0644)
	if err != nil {
		panic(err)
	}

}

type Callback interface {
	OnResponse(data []byte)
	OnError(err string)
}

type KReader interface {
	io.Reader
}
