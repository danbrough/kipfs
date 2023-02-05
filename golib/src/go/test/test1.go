package main

import (
	"github.com/danbrough/kipfs/misc"
	"github.com/danbrough/kipfs/shell"
	"github.com/multiformats/go-multibase"
)

func main() {
	println("running test1")


	println("The time is: " +misc.GetTime())

	println(multibase.Encode(multibase.Base64url, []byte("testing")))

	println("BaseEmoji", multibase.Base256Emoji)

	var ipfsURL = "/ip4/127.0.0.1/tcp/5001"
	s := shell.NewShell(ipfsURL)

	rb := s.NewRequest("multibase/encode")
	
	rb.StringOptions("b", "base64url")
	
	req, err := rb.PostString2("123")

	if err != nil {
		panic(err.Error())
	}
	println("REceived", string(req), "length:", len(req))

}
