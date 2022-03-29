package main

import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/core"
	"github.com/danbrough/kipfs/misc"
)

func main() {
	println("The message is : " + misc.GetMessage())
	println("The message2 is  : " + misc.GetMessage2())
	println("DAG is  : " + cids.DagCid(`"Hello World"`))
	shell := core.NewShell("/ip4/192.168.1.4/tcp/5001")
	data, err := shell.NewRequest("id").Send()
	if err != nil {
		println("Failed ", err.Error())
		return
	}
	println(string(data))

}
