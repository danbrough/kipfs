package test

import "testing"

func TestIpfsID(t *testing.T) {
	tests := []struct {
		name string
	}{
		// TODO: Add test cases.
		{"fred"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			IpfsID()
		})
	}
}
