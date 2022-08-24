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

	println("BaseEmoji", multibase.Base256Emoji)

	rb := s.NewRequest("multibase")
	rb.Argument("encode")
	rb.StringOptions("b","base64urld")
	rb.BodyString("111111111111111111111111")
	req,err = rb.Send()
	if err != nil {
		panic(err.Error())
	}
	println("REceived",string(req),"length:",len(req))

}
