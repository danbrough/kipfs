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
	"github.com/multiformats/go-multibase"

	"unsafe"

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

//export KRequest
func KRequest(refnum C.int32_t, command *C.char, arg *C.char) (unsafe.Pointer, int, *C.char) {
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	kShell := ref.Get().(*shell.Shell)

	println("creating request")
	rb := kShell.NewRequest(C.GoString(command))
	if arg != nil {
		goArg := C.GoString(arg)
		println("Adding Argument:", goArg)
		rb.Argument(goArg)
	}

	println("Sending request ..")
	data, err := rb.Send()
	if err != nil {
		return nil, -1, C.CString(err.Error())
	}
	println("returning data")

	return C.CBytes(data), len(data), nil
}

//export KMultiBaseEncode
func KMultiBaseEncode(encoding C.int32_t, cData *C.char, dataLength C.int32_t) (*C.char, *C.char) {
	//	multibase.Encode(encoding, C.B)

	data := C.GoBytes(unsafe.Pointer(cData), dataLength)

	encoded, err := multibase.Encode(multibase.Encoding(encoding), data)
	if err != nil {
		return nil, C.CString(err.Error())
	}
	println("GOT DATA: ", string(data))
	return C.CString(encoded), nil
}

//export KMultiBaseDecode
func KMultiBaseDecode(cData *C.char, dataLength C.int32_t) (C.int32_t, *C.char, C.int32_t, *C.char) {

	encoding, data, err := multibase.Decode(C.GoStringN(cData, dataLength))
	if err != nil {
		return -1, nil, -1, C.CString(err.Error())
	}
	return C.int32_t(encoding), C.CString(string(data)), C.int32_t(len(data)), nil
}

func main() {}
