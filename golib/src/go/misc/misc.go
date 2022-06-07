package misc

import (
	"time"
)

func GetTime() string {
	return time.Now().Local().String()
}
