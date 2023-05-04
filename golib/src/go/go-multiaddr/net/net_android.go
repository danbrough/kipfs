//go:build android
// +build android

package manet

import "net"

type androidInterface struct{}

// @FIXME(gfanton): on android sdk30, syscall from `net.InterfaceAddrs()` are restricted, manually return localhost on android
func (androidInterface) InterfaceAddrs() ([]net.Addr, error) {
	localhost, _ := net.ResolveIPAddr("ip", "127.0.0.1")
	return []net.Addr{localhost}, nil
}

func getNetDriver() NetDriver {
	return &androidInterface{}
}
