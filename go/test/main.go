package main

import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/misc"
	"github.com/danbrough/kipfs/shell"
)

func main() {
	println("The message is : " + misc.GetMessage())
	println("The message2 is  : " + misc.GetMessage2())
	println("DAG is  : " + cids.DagCid(`"Hello World"`))
	ipfsShell := shell.NewShell("/ip4/192.168.1.4/tcp/5001")
	data, err := ipfsShell.NewRequest("id").Send()
	if err != nil {
		println("Failed ", err.Error())
		return
	}
	println(string(data))

}
