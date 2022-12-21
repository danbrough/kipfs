package misc

import "C"

import (
	"io"
	"os"
	"time"
)

type Reader = io.Reader
type Closer = io.Closer
type ReadCloser = io.ReadCloser

func WriteStuff(data []byte, path string) {
	//log.Infof("Writing stuff to %s", path)
	err := os.WriteFile(path, data, 0644)
	if err != nil {
		panic(err)
	}

}

type Callback interface {
	OnResponse(data []byte)
	OnError(err string)
}

type KReader interface {
	io.Reader
}

type Printer interface {
	Print(msg string)
}

func test(data []int8) {
	//	t := System.CurrentTimeMillis()
	//println("T is", t)
	for _, b := range data {
		println("B is", b)
	}
}

func Test2(data []byte) {
	var data2 []int8
	data2 = make([]int8, len(data))
	for i, b := range data {
		data2[i] = int8(b)
	}
	test(data2)
}

func TestPrinter(printer Printer) {
	msg := GetTime()
	printer.Print("Printing message: " + msg)
}

func GetTime() string {
	//location, _ := time.LoadLocation("NZ")

	t := time.Now()
	msg := t.Local().Format("15:04:05 2006/01/02")
	return msg
}

func GetMessage2() string {
	//location, _ := time.LoadLocation("NZ")

	t := time.Now()
	msg := t.Local().Format("15:04:05 2006/01/02")
	return msg
}

func GetMessage3() string {
	//location, _ := time.LoadLocation("NZ")

	t := time.Now()
	msg := t.Local().Format("15:04:05 2006/01/02")
	return "Message3:" + msg
}
