//go:build shell
// +build shell

package main

// #cgo CFLAGS: -fPIC
import "C"
import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/misc"
	ipfsapi "github.com/ipfs/go-ipfs-api"
)

var shell *ipfsapi.Shell

//export KCID
func KCID(json *C.char) *C.char {
	return C.CString(cids.DagCid(C.GoString(json)))
}

//export KCreateShell
func KCreateShell(url *C.char) {
	if shell == nil {
		println("creating shell...")
		shell = ipfsapi.NewShell(C.GoString(url))
	} else {
		println("ERROR: shell already exists")
	}
}

//export KCloseShell
func KCloseShell() {
	if shell != nil {
		shell = nil
	}

}

//export KGetMessage
func KGetMessage() *C.char {
	return C.CString(misc.GetMessage())
}

//export KGetMessage2
func KGetMessage2() *C.char {
	return C.CString(misc.GetMessage2())
}

//export KGetMessage3
func KGetMessage3() *C.char {
	return C.CString(misc.GetMessage3())
}

//export KIpfsID
func KIpfsID() *C.char {
	if shell == nil {
		return nil
	}

	s, err := shell.ID()
	if err != nil {
		return C.CString(err.Error())
	}

	return C.CString(s.ID)
}

//export KRequest
func KRequest(command *C.char) {
	if shell == nil {
		return
	}
}
