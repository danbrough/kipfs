package main

import (
	"context"
	"io"

	ipfsapi "github.com/ipfs/go-ipfs-api"
)

func main() {
	println("running test2")

	var ipfsURL = "/ip4/127.0.0.1/tcp/5001"
	s := ipfsapi.NewShell(ipfsURL)
	rb := s.Request("id")
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
