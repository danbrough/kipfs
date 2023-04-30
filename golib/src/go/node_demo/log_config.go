

package main

import (

	// ipfs_fsrepo "github.com/ipfs/kubo/repo/fsrepo"
	log "github.com/sirupsen/logrus"
)

func init() {
	log.SetFormatter(&log.TextFormatter{
		FullTimestamp: true,
		ForceColors: true,
		TimestampFormat: "15:04:05:06",
	})
	log.StandardLogger().Level = log.TraceLevel
}
