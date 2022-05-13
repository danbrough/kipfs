//go:build shell
// +build shell

package main

/*
#cgo CFLAGS: -fPIC
#include "defs.h"
*/
import "C"
import (
	"github.com/danbrough/kipfs/cids"
	"github.com/danbrough/kipfs/misc"
	"github.com/danbrough/kipfs/shell"
	_seq "golang.org/x/mobile/bind/seq"
	"unsafe"
)

//export KCallbackTest
func KCallbackTest(callback C.DataCallbackFunc) {
	println("GOT CALLBACK", callback)
}

//export KCID
func KCID(json *C.char) *C.char {
	return C.CString(cids.DagCid(C.GoString(json)))
}

//export KDestroyRef
func KDestroyRef(refnum C.int32_t) {
	_seq.Delete(int32(refnum))
}

//export KCreateShell
func KCreateShell(cUrl *C.char) (C.int32_t, *C.char) {
	var url = C.GoString(cUrl)
	println("KCreateShell():", url)
	var ptr C.int32_t = _seq.NullRefNum
	println("_seq.NullRefNum is", ptr)

	println("creating shell....")
	kShell := shell.NewShell(url)

	if kShell != nil {
		refnum := _seq.ToRefNum(kShell)
		_seq.Inc(refnum)
		ptr = C.int32_t(refnum)
		println("returning refnum", ptr)
	} else {
		return ptr, C.CString("Failed to created shell")
	}

	return ptr, nil
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

//export KTest
func KTest() unsafe.Pointer {
	return C.CBytes([]byte("123abc$"))
}

//export KRequest
func KRequest(refnum C.int32_t, name *C.char) (unsafe.Pointer, int, *C.char) {
	ref := _seq.FromRefNum(int32(refnum))
	kShell := ref.Get().(*shell.Shell)

	s, err := kShell.NewRequest(C.GoString(name)).Send()
	if err != nil {
		return nil, -1, C.CString(err.Error())
	}

	return C.CBytes(s), len(s), nil
}
