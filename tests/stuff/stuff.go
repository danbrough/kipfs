package stuff

import "time"


func TestMessage() string {
	return "Time is: " + time.Now().String()
}