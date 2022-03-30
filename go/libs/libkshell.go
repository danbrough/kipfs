//go:build shell
// +build shell

package main

// #cgo CFLAGS: -fPIC
import "C"
import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/core"
	"github.com/danbrough/kipfs/misc"
)

var shell *core.Shell

//export KCID
func KCID(json *C.char) *C.char {
	return C.CString(cids.DagCid(C.GoString(json)))
}

//export KCreateShell
func KCreateShell(url *C.char) {
	if shell == nil {
		println("creating shell....")
		shell = core.NewShell(C.GoString(url))
	} else {
		println("ERROR: shell already exists")
	}

}

//export KCloseShell
func KCloseShell() {
	if shell != nil {
		println("closing shell...")
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

//export KCmdID
func KCmdID() *C.char {
	if shell == nil {
		return nil
	}

	s, err := shell.NewRequest("id").Send()
	if err != nil {
		return C.CString(err.Error())
	}

	return C.CString(string(s))
}

type KResponse struct {
	data  *C.char
	error *C.char
}

//export KCmdID2
func KCmdID2() (*C.char, *C.char) {
	if shell == nil {
		return nil, C.CString("shell is null")
	}

	s, err := shell.NewRequest("id").Send()
	if err != nil {
		return nil, C.CString(err.Error())
	}

	println("RECEIVED: ", string(s))
	return C.CString(string(s)), nil
}
