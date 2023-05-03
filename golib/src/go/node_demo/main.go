package main

import (
	"fmt"

	//"path/filepath"
	//"sync"

	//ipfs_mobile "github.com/danbrough/kipfs/ipfs_mobile"
	//"github.com/danbrough/kipfs/core"
	//ipfs_loader "github.com/ipfs/kubo/plugin/loader"

	// ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"
	"github.com/danbrough/kipfs/core"

	//"github.com/danbrough/kipfs/node"

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
	repoInitialized := core.RepoIsInitialized(repoPath)
	log.Infof("Repo: %s is initialized: %v", repoPath, repoInitialized)

	if ! repoInitialized{
		log.Info("initializing repo at " + repoPath)
	}
	//ctx := context.Background()

	//fmt.Println("Repo initialized: ", ipfs_fsrepo.IsInitialized("/home/dan/.ipfs"))
	//fmt.Fprintln("Conf is %v",conf.String())
}
