package main

import (
	"fmt"
	"path/filepath"
	"sync"

	//ipfs_mobile "github.com/danbrough/kipfs/ipfs_mobile"
	"github.com/danbrough/kipfs/core"
	ipfs_loader "github.com/ipfs/kubo/plugin/loader"

	// ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"

	log "github.com/sirupsen/logrus"
)

func main() {

	log.Info("Node Demo()")
	log.Debug("debug message")
	log.Trace("trace message")

	conf, err := core.NewDefaultConfig()
	if err != nil {
		panic(err)
	}
	println("Conf", conf.String())
	fmt.Printf("Int is %d\n", 123)

	repoPath := "/tmp/ipfs"
	log.Infof("Repo: %s is initialized: %v", repoPath, core.RepoIsInitialized(repoPath))

	//fmt.Println("Repo initialized: ", ipfs_fsrepo.IsInitialized("/home/dan/.ipfs"))
	//fmt.Fprintln("Conf is %v",conf.String())
}

var (
	muPlugins sync.Mutex
	plugins   *ipfs_loader.PluginLoader
)

func loadPlugins(repoPath string) (*ipfs_loader.PluginLoader, error) {
	muPlugins.Lock()
	defer muPlugins.Unlock()

	if plugins != nil {
		return plugins, nil
	}

	pluginpath := filepath.Join(repoPath, "plugins")

	lp, err := ipfs_loader.NewPluginLoader(pluginpath)
	if err != nil {
		return nil, err
	}

	if err = lp.Initialize(); err != nil {
		return nil, err
	}

	if err = lp.Inject(); err != nil {
		return nil, err
	}

	plugins = lp
	return lp, nil
}
