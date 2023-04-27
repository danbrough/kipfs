package core

import (
	"path/filepath"
	"sync"
	//ipfs_mobile "github.com/danbrough/kipfs/ipfs_mobile"
	ipfs_loader "github.com/ipfs/kubo/plugin/loader"
//	ipfs_repo "github.com/ipfs/kubo/repo"
	ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"
)

var (
	muPlugins sync.Mutex
	plugins   *ipfs_loader.PluginLoader
)

func RepoIsInitialized(path string) bool {
	return ipfs_fsrepo.IsInitialized(path)
}
