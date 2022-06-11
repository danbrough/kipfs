package main

import (
	"github.com/danbrough/kipfs/shell"
	"github.com/multiformats/go-multibase"
)

func main() {
	s := shell.NewShell("/ip4/127.0.0.1/tcp/5001")
	req, err := s.NewRequest("id").Send()
	if err != nil {
		panic(err.Error())
	}
	println("Received: ", string(req))

	println(multibase.Encode(multibase.Base64url, []byte("testing")))

}
