package main

import (
	"fmt"

	"github.com/danbrough/kipfs/core"
//	ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"
)

func main() {
	println("Node demo")

	conf,err := core.NewDefaultConfig()
	if err != nil {
		panic(err)
	}
	println("Conf",conf)
	fmt.Printf("Int is %d\n",123)

	//fmt.Println("Repo initialized: ", ipfs_fsrepo.IsInitialized("/home/dan/.ipfs"))
	//fmt.Fprintln("Conf is %v",conf.String())
}