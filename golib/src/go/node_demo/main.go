package main

import (
	"fmt"
	//"path/filepath"
	//"sync"
	"github.com/ipfs/kubo/plugin/loader"

	//ipfs_mobile "github.com/danbrough/kipfs/ipfs_mobile"
	//"github.com/danbrough/kipfs/core"
	//ipfs_loader "github.com/ipfs/kubo/plugin/loader"
	
	// ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"
	"github.com/danbrough/kipfs/core"
	

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
	log.Infof("Repo: %s is initialized: %v", repoPath,core.RepoIsInitialized(repoPath))

	loadPlugins(repoPath)

	//fmt.Println("Repo initialized: ", ipfs_fsrepo.IsInitialized("/home/dan/.ipfs"))
	//fmt.Fprintln("Conf is %v",conf.String())
}



func loadPlugins(repoPath string) (*loader.PluginLoader, error) {
	plugins, err := loader.NewPluginLoader(repoPath)
	if err != nil {
		return nil, fmt.Errorf("error loading plugins: %s", err)
	}

	if err := plugins.Initialize(); err != nil {
		return nil, fmt.Errorf("error initializing plugins: %s", err)
	}

	if err := plugins.Inject(); err != nil {
		return nil, fmt.Errorf("error initializing plugins: %s", err)
	}
	return plugins, nil
}
