package main

/*
#cgo CFLAGS: -fPIC
*/
import "C"
import "github.com/danbrough/golibtest/misc"


//export GetTime
func GetTime() *C.char {
	return C.CString("Local time: " + misc.GetTime())
}

func main() {}
