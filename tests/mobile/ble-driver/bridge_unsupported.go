//go:build !darwin && !android
// +build !darwin,!android

package ble

import (
	"danbrough.org/kipfs/mobile/proximitytransport"
	"go.uber.org/zap"
)

const Supported = false

// Noop implementation for platform that are not Darwin

func NewDriver(logger *zap.Logger) proximitytransport.ProximityDriver {
	logger = logger.Named("BLE")
	logger.Info("NewDriver(): incompatible system")

	return proximitytransport.NewNoopProximityDriver(ProtocolCode, ProtocolName, DefaultAddr)
}
