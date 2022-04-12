// Copyright 2016 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
//go:build shell
// +build shell
package main

// Go support functions for generated Go bindings. This file is
// copied into the generated main package, and compiled along
// with the bindings.



/*
#cgo CFLAGS: -fPIC
#include <stdlib.h>
#include <stdint.h>
*/
import "C"

import (
	"fmt"
	_seq "golang.org/x/mobile/bind/seq"
)

// IncGoRef is called by foreign code to pin a Go object while its refnum is crossing
// the language barrier
//export IncGoRef
func IncGoRef(refnum C.int32_t) {
	_seq.Inc(int32(refnum))
}

func init() {
    println("seq.go init()")
	_seq.FinalizeRef = func(ref *_seq.Ref) {
	    println("finalize ref")
		refnum := ref.Bind_Num
		if refnum < 0 {
			panic(fmt.Sprintf("not a foreign ref: %d", refnum))
		}
		println("C.go_seq_dec_ref(C.int32_t(refnum))")
		//C.go_seq_dec_ref(C.int32_t(refnum))
	}
	_seq.IncForeignRef = func(refnum int32) {
	    println("increment ref")
		if refnum < 0 {
			panic(fmt.Sprintf("not a foreign ref: %d", refnum))
		}
		println("C.go_seq_inc_ref(C.int32_t(refnum))")
		//C.go_seq_inc_ref(C.int32_t(refnum))
	}
	// Workaround for issue #17393.
	//signal.Notify(make(chan os.Signal), syscall.SIGPIPE)
}

//func main() {}
