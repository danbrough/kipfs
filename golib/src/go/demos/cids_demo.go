package main


import "github.com/danbrough/kipfs/cids"

func main(){
	s := cids.DagCid(`"Hello World"`)
	println("Cid Test:" + s)
}

