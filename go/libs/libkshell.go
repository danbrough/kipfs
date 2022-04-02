//go:build shell
// +build shell

package main

// #cgo CFLAGS: -fPIC
import "C"
import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/misc"
	"github.com/danbrough/kipfs/shell"
	"unsafe"
)

var ipfsShell *shell.Shell

//export KCID
func KCID(json *C.char) *C.char {
	return C.CString(cids.DagCid(C.GoString(json)))
}

//export KCreateShell
func KCreateShell(url *C.char) {
	if ipfsShell == nil {
		println("creating shell....")
		ipfsShell = shell.NewShell(C.GoString(url))
	} else {
		println("ERROR: shell already exists")
	}

}

//export KCloseShell
func KCloseShell() {
	if ipfsShell != nil {
		println("closing shell...")
		ipfsShell = nil
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
	if ipfsShell == nil {
		return nil
	}

	s, err := ipfsShell.NewRequest("id").Send()
	if err != nil {
		return C.CString(err.Error())
	}

	return C.CString(string(s))
}

//export KRequest
func KRequest(name *C.char) ([]byte, *C.char) {
	if ipfsShell == nil {
		return nil, C.CString("ipfsShell is nil")
	}

	s, err := ipfsShell.NewRequest(C.GoString(name)).Send()
	if err != nil {
		return nil, C.CString(err.Error())
	}

	return s, nil
	//return C.CString(string(s))

}

//export KTest
func KTest() unsafe.Pointer {
	return C.CBytes([]byte("123abc$"))
}

//export KRequest2
func KRequest2(name *C.char) (unsafe.Pointer, int,*C.char) {
	if ipfsShell == nil {
		println("return error as ipfsShell is nil")
		return nil, -1,C.CString("ipfsShell is nil")
	}

	s, err := ipfsShell.NewRequest(C.GoString(name)).Send()
	if err != nil {
		return nil, -1,C.CString(err.Error())
	}


	println("returning bytes..")
	return C.CBytes(s),len(s) ,nil
	//return C.CString(string(s))

}

//export KCmdID2
func KCmdID2() (*C.char, *C.char) {
	if ipfsShell == nil {
		return nil, C.CString("shell is null")
	}

	s, err := ipfsShell.NewRequest("id").Send()
	if err != nil {
		return nil, C.CString(err.Error())
	}

	println("RECEIVED: ", string(s))
	return C.CString(string(s)), nil
}

