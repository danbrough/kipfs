//go:build shell
// +build shell

package main

/*
#cgo CFLAGS: -fPIC
#include "defs.h"


enum PostDataType {
  Raw,
  StringFile,
  BytesFile
};


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

//export KCallbackTest
func KCallbackTest(callback C.DataCallbackFunc) {
	println("KCallbackTest()")
	C.bridgeDataCallback(callback, unsafe.Pointer(C.CString("Hello from here")), 1234, nil)
}

//export KTest
func KTest() unsafe.Pointer {
	return C.CBytes([]byte("123abc$"))
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


//export KCreateRequest
func KCreateRequest(refnum C.int32_t, command *C.char,arg *C.char) (C.int32_t, *C.char) {
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	kShell := ref.Get().(*shell.Shell)

	//println("KCreateShell():", url)
	var ptr C.int32_t = _seq.NullRefNum
	//println("_seq.NullRefNum is", ptr)

	rb := kShell.NewRequest(C.GoString(command))


	if rb != nil {
		refnum := _seq.ToRefNum(rb)
		_seq.Inc(refnum)
		ptr = C.int32_t(refnum)
		//println("returning refnum", ptr)
	} else {
		return ptr, C.CString("Failed to created shell ")
	}

	if arg != nil {
		goArg := C.GoString(arg)
		println("Adding Argument:", goArg)
		rb.Argument(goArg)
	}

	return ptr, nil
}


//export KRequestSend
func KRequestSend(refnum C.int32_t) (unsafe.Pointer, int, *C.char){
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	rb := ref.Get().(*shell.RequestBuilder)
	data, err := rb.Send()
	
	if err != nil {
		return nil, -1, C.CString(err.Error())
	}

	return C.CBytes(data), len(data), nil
}

//export KRequestPostString
func KRequestPostString(refnum C.int32_t,data *C.char) (unsafe.Pointer, int, *C.char){
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	rb := ref.Get().(*shell.RequestBuilder)
	
	respData, err := rb.PostString2(C.GoString(data))
	
	if err != nil {
		return nil, -1, C.CString(err.Error())
	}

	return C.CBytes(respData), len(respData), nil
}


//export KRequestPostBytes
func KRequestPostBytes(refnum C.int32_t,data *C.char,dataLength C.int32_t) (unsafe.Pointer, int, *C.char){
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	rb := ref.Get().(*shell.RequestBuilder)
	
	respData, err := rb.PostData2(C.GoBytes(unsafe.Pointer(data),dataLength))
	
	if err != nil {
		return nil, -1, C.CString(err.Error())
	}

	return C.CBytes(respData), len(respData), nil
}

//export KRequestPostBytes2
func KRequestPostBytes2(refnum C.int32_t,data *C.char,dataLength C.int32_t) (unsafe.Pointer, int, *C.char){
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	rb := ref.Get().(*shell.RequestBuilder)

	respData, err := rb.PostData2(C.GoBytes(unsafe.Pointer(data),dataLength))

	if err != nil {
		return nil, -1, C.CString(err.Error())
	}

	return C.CBytes(respData), len(respData), nil
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

//export KRequestOption
func KRequestOption(refnum C.int32_t, name *C.char, value *C.char) {
	_seq.Inc(int32(refnum))
	ref := _seq.FromRefNum(int32(refnum))
	rb := ref.Get().(*shell.RequestBuilder)
	rb.StringOptions(C.GoString(name),C.GoString(value))
}

//export KMultiBaseEncode
func KMultiBaseEncode(encoding C.int32_t, cData *C.char, dataLength C.int32_t) (*C.char, *C.char) {

	data := C.GoBytes(unsafe.Pointer(cData), dataLength)

	encoded, err := multibase.Encode(multibase.Encoding(encoding), data)
	if err != nil {
		return nil, C.CString(err.Error())
	}
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
