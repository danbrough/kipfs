//go:build !android
// +build !android

package manet

import "net"

type defaultInterface struct{}

func (defaultInterface) InterfaceAddrs() ([]net.Addr, error) { return net.InterfaceAddrs() }

func getNetDriver() NetDriver {
	return &defaultInterface{}
}
