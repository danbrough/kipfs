package core

import (
	proximity "github.com/danbrough/kipfs/proximitytransport"
)

type ProximityDriver interface {
	proximity.ProximityDriver
}

type ProximityTransport interface {
	proximity.ProximityTransport
}

func GetProximityTransport(protocolName string) (t ProximityTransport) {
	proximity.TransportMapMutex.RLock()
	t = proximity.TransportMap[protocolName]
	proximity.TransportMapMutex.RUnlock()
	return
}
