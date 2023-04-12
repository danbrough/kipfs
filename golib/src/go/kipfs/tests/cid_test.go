package tests

import (
	"fmt"
	"testing"

	"github.com/danbrough/kipfs/cids"
)

// TestHelloName calls greetings.Hello with a name, checking
// for a valid return value.
func TestHelloName(t *testing.T) {
	fmt.Printf("Hello there: %v\n", "Frank")
	t.Logf("Hello again: %v\n","Dude")
}


func TestDagCid(t *testing.T) {
	s := cids.DagCid(`"Hello World"`)
	fmt.Printf("CID is %v\n",s)
}
