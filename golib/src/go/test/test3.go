package main

import (
	"context"
	"io"
	"strings"

	ipfsapi "github.com/ipfs/go-ipfs-api"
	files "github.com/ipfs/go-ipfs-files"
)


func test1(){
	var ipfsURL = "/ip4/127.0.0.1/tcp/5001"
	s := ipfsapi.NewShell(ipfsURL)
	rb := s.Request("multibase/encode")
	rb.Option("b","base64url")

	fr := files.NewReaderFile(strings.NewReader("123"))
	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry("", fr)})
	fileReader := files.NewMultiFileReader(slf, true)
	rb.Body(fileReader)

	res,err := rb.Send(context.Background())
	if err != nil {
		panic(err)
	}
	data,err := io.ReadAll(res.Output)
	if err != nil {
		panic(err)
	}

	res.Close()

	println(string(data))
}
func main() {
	println("running test3")

	test1()
	

}
