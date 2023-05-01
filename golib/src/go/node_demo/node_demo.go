package main

import (
	"fmt"
	//"path/filepath"
	//"sync"

	//ipfs_mobile "github.com/danbrough/kipfs/ipfs_mobile"
	//"github.com/danbrough/kipfs/core"
	//ipfs_loader "github.com/ipfs/kubo/plugin/loader"
	
	// ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"

	log "github.com/sirupsen/logrus"
)

func main() {

	log.Info("Node Demo()")
	log.Debug("debug message")
	log.Trace("trace message")

	conf, err := NewDefaultConfig()
	if err != nil {
		panic(err)
	}
	println("Conf", conf.String())
	fmt.Printf("Int is %d\n", 123)

	repoPath := "/tmp/ipfs"
	log.Infof("Repo: %s is initialized: %v", repoPath,true)

	//fmt.Println("Repo initialized: ", ipfs_fsrepo.IsInitialized("/home/dan/.ipfs"))
	//fmt.Fprintln("Conf is %v",conf.String())
}
