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

//export KDecRef
func KDecRef(refnum C.int32_t) {
	_seq.Delete(int32(refnum))
}

//export KIncGoRef
func KIncGoRef(refnum C.int32_t) {
	_seq.Inc(int32(refnum))
}

func main() {}
