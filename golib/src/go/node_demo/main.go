package main

import (
	//"fmt"

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

	repoPath := "/tmp/ipfs"
	repoInitialized := core.RepoIsInitialized(repoPath)
	log.Infof("Repo: %s is initialized: %v", repoPath, repoInitialized)
	log.Warn("warning about something..")
	log.Error("printing an error about something ..")

	if !repoInitialized {
		log.Info("initializing repo at " + repoPath)
		var conf *core.Config
		var err error
		conf, err = core.NewDefaultConfig()
		if err != nil {
			panic(err)
		}
		log.Infof("Conf: %s", conf.String())

		err = core.InitRepo(repoPath, conf)
		if err != nil {
			panic(err)
		}
	}

	repo, err := core.OpenRepo(repoPath)
	if err != nil {
		panic(err)
	}

	defer func() {
		log.Info("Closing repo..")
		repo.Close()
	}()

	log.Infof("opened repo: %s", repo)

	n,err := core.NewNode(repo,nil)
	if err != nil {
		panic(err)
	}

	log.Info("created node ")
	println("n",n)

	defer func() {
		log.Info("Closing node..")
		n.Close()
	}()

}
