package main

/*
#include <stdio.h>
#include <stdlib.h>
#include "defs.h"



*/
import "C"
import "fmt"
import "unsafe"

func main() {

	data := []byte("Hello World")
	callback := C.DataCallbackFunc(C.testCallback)
	cdata := C.CBytes(data)
	C.bridgeDataCallback(callback, cdata, C.int(len(data)), nil)
	err := C.CString("This is the error")
	C.bridgeDataCallback(callback, cdata, -1, err)
	C.free(cdata)
	C.free(unsafe.Pointer(err))
	fmt.Println("done")
}
