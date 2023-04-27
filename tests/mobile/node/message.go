package node 

import "time"


func TestMessage() string {
	return "Time is " + time.Now().String()
}