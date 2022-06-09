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
)

//export GetTime
func GetTime() *C.char {
	return C.CString(misc.GetTime())
}

//export KCID
func KCID(json *C.char) *C.char {
	return C.CString(cids.DagCid(C.GoString(json)))
}

//export KIncGoRef
func KIncGoRef(refnum C.int32_t) {
	_seq.Inc(int32(refnum))
}

//export KDecRef
func KDecRef(refnum C.int32_t) {
	_seq.Delete(int32(refnum))
}

//export KCreateShell
func KCreateShell(cUrl *C.char) (C.int32_t, *C.char) {
	var url = C.GoString(cUrl)
	//println("KCreateShell():", url)
	var ptr C.int32_t = _seq.NullRefNum
	//println("_seq.NullRefNum is", ptr)

	//println("creating shell....")
	kShell := shell.NewShell(url)

	if kShell != nil {
		refnum := _seq.ToRefNum(kShell)
		_seq.Inc(refnum)
		ptr = C.int32_t(refnum)
		//println("returning refnum", ptr)
	} else {
		return ptr, C.CString("Failed to created shell")
	}

	return ptr, nil
}

func main() {}
