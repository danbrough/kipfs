// sockmanager manage sock path to keep it short

//go:build windows
// +build windows !linux

package core

import (
  "errors"
  "sync"
)

type SockManager struct {
  sockDirPath string
  counter     uint32
  muCounter   sync.Mutex
}

func NewSockManager(path string) (*SockManager, error) {
  return nil, errors.New("not implemented")
}

func (sm *SockManager) NewSockPath() (string, error) {
  return "", errors.New("not implemented")
}
