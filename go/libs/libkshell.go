//go:build shell
// +build shell

package main

/*
#cgo CFLAGS: -fPIC
#include <stdlib.h>
#include <stdint.h>
*/
import "C"
import (
	"fmt"
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/misc"
	"github.com/danbrough/kipfs/shell"
	_seq "golang.org/x/mobile/bind/seq"
	"unsafe"
)

func init() {
	_seq.FinalizeRef = func(ref *_seq.Ref) {
		refnum := ref.Bind_Num
		if refnum < 0 {
			panic(fmt.Sprintf("not a foreign ref: %d", refnum))
		}
		println("Finalizing ", refnum)
		//C.go_seq_dec_ref(C.int32_t(refnum))
	}
	_seq.IncForeignRef = func(refnum int32) {
		if refnum < 0 {
			panic(fmt.Sprintf("not a foreign ref: %d", refnum))
		}
		println("Incrementing ref to", refnum)
		//C.go_seq_inc_ref(C.int32_t(refnum))
	}
	// Workaround for issue #17393.
	//signal.Notify(make(chan os.Signal), syscall.SIGPIPE)
}

// IncGoRef is called by foreign code to pin a Go object while its refnum is crossing
// the language barrier
//export IncGoRef
func IncGoRef(refnum C.int32_t) {
	_seq.Inc(int32(refnum))
}

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

//export KDestroyRef
func KDestroyRef(refnum C.int32_t) {
	_seq.Delete(int32(refnum))
}

//export KCreateShell2
func KCreateShell2(cUrl *C.char) (C.int32_t, *C.char) {
	var url = C.GoString(cUrl)
	println("KCreateShell2():", url)
	var ptr C.int32_t = _seq.NullRefNum
	println("_seq.NullRefNum is", ptr)

	println("creating shell....")
	kShell := shell.NewShell(url)

	if kShell != nil {
		ptr = C.int32_t(_seq.ToRefNum(kShell))
		println("returning refnum", ptr)
	}

	return ptr, nil
}

//export KTest2
func KTest2(refnum C.int32_t) {
	println("KTest2() refNum: ", refnum)
	ref := _seq.FromRefNum(int32(refnum))
	kShell := ref.Get().(*shell.Shell)
	println("GOT SHELL ", kShell)
	data, err := kShell.NewRequest("id").Send()
	if err != nil {
		println("Request failed:", err.Error())
		return
	}
	println("Response:", string(data))
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
func KRequest2(name *C.char) (unsafe.Pointer, int, *C.char) {
	if ipfsShell == nil {
		println("return error as ipfsShell is nil")
		return nil, -1, C.CString("ipfsShell is nil")
	}

	s, err := ipfsShell.NewRequest(C.GoString(name)).Send()
	if err != nil {
		return nil, -1, C.CString(err.Error())
	}

	println("returning bytes..")
	return C.CBytes(s), len(s), nil
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
