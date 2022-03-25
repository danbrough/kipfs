package main

import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/misc"
)

func main() {
	println("The message is : " + misc.GetMessage())
	println("The message2 is  : " + misc.GetMessage2())
	println("DAG is  : " + cids.DagCid(`"Hello World"`))

}
