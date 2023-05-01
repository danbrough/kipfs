package main

import (
	// "encoding/base64"
	//"errors"
	// "fmt"
	"io"
	"os"

	"github.com/ipfs/interface-go-ipfs-core/options"
	log "github.com/sirupsen/logrus"

	ipfs_config "github.com/ipfs/kubo/config"
	//libp2p_ci "github.com/libp2p/go-libp2p-core/crypto"
	//libp2p_peer "github.com/libp2p/go-libp2p-core/peer"
)



func initConfig(out io.Writer, nBitsForKeypair int) (*ipfs_config.Config, error) {
	identity, err := identityConfig(out, nBitsForKeypair)
	if err != nil {
		return nil, err
	}

	bootstrapPeers, err := ipfs_config.DefaultBootstrapPeers()
	if err != nil {
		return nil, err
	}

	datastore := DefaultDatastoreConfig()

	conf := &ipfs_config.Config{
		API: ipfs_config.API{
			HTTPHeaders: map[string][]string{},
		},

				// setup the node's default addresses.
		// NOTE: two swarm listen addrs, one tcp, one utp.
		Addresses: addressesConfig(),

		Datastore: datastore,
		Bootstrap: ipfs_config.BootstrapPeerStrings(bootstrapPeers),
		Identity: identity,
		Discovery: ipfs_config.Discovery{
			MDNS: ipfs_config.MDNS{
				Enabled:  true,
			},
		},

		Routing: ipfs_config.Routing{
			Type:  ipfs_config.NewOptionalString("dhtclient"),
		},
		Mounts: ipfs_config.Mounts{
			IPFS: "/ipfs",
			IPNS: "/ipns",
		},

		Ipns: ipfs_config.Ipns{
			ResolveCacheSize: 128,
		},

		Gateway: ipfs_config.Gateway{
			RootRedirect: "",
			NoFetch:      false,
			PathPrefixes: []string{},
			HTTPHeaders: map[string][]string{
				"Access-Control-Allow-Origin":  {"*"},
				"Access-Control-Allow-Methods": {"GET"},
				"Access-Control-Allow-Headers": {"X-Requested-With", "Range", "User-Agent"},
			},
			APICommands: []string{},
		},

		Reprovider: ipfs_config.Reprovider{
			Interval: nil,
			Strategy: nil,
		},

		Pinning: ipfs_config.Pinning{
			RemoteServices: map[string]ipfs_config.RemotePinningService{},
		},

		DNS: ipfs_config.DNS{
			Resolvers: map[string]string{},
		},

		Migration: ipfs_config.Migration{
			DownloadSources: []string{},
			Keep:            "",
		},
	}

/*
	conf := &ipfs_config.Config{


		Reprovider: ipfs_config.Reprovider{
			Interval: "0",
			Strategy: "all",
		},
		Swarm: ipfs_config.SwarmConfig{
			ConnMgr: ipfs_config.ConnMgr{
				LowWater:    defaultConnMgrLowWater,
				HighWater:   defaultConnMgrHighWater,
				GracePeriod: defaultConnMgrGracePeriod.String(),
				Type:        "basic",
			},
		},
	}
	*/

	return conf, nil
}


func addressesConfig() ipfs_config.Addresses {
	return ipfs_config.Addresses{
		Swarm: []string{
			"/ip4/0.0.0.0/tcp/4001",
			"/ip6/::/tcp/4001",
			"/ip4/0.0.0.0/udp/4001/quic",
			"/ip4/0.0.0.0/udp/4001/quic-v1",
			"/ip4/0.0.0.0/udp/4001/quic-v1/webtransport",
			"/ip6/::/udp/4001/quic",
			"/ip6/::/udp/4001/quic-v1",
			"/ip6/::/udp/4001/quic-v1/webtransport",
		},
		Announce:       []string{},
		AppendAnnounce: []string{},
		NoAnnounce:     []string{},
		API:            ipfs_config.Strings{"/ip4/127.0.0.1/tcp/5001"},
		Gateway:        ipfs_config.Strings{"/ip4/127.0.0.1/tcp/8080"},
	}
}

func DefaultDatastoreConfig() ipfs_config.Datastore {
	return  ipfs_config.Datastore{
		StorageMax:         "10GB",
		StorageGCWatermark: 90, // 90%
		GCPeriod:           "1h",
		BloomFilterSize:    0,
		Spec:               flatfsSpec(),
	}
}

func flatfsSpec() map[string]interface{} {
	return map[string]interface{}{
		"type": "mount",
		"mounts": []interface{}{
			map[string]interface{}{
				"mountpoint": "/blocks",
				"type":       "measure",
				"prefix":     "flatfs.datastore",
				"child": map[string]interface{}{
					"type":      "flatfs",
					"path":      "blocks",
					"sync":      true,
					"shardFunc": "/repo/flatfs/shard/v1/next-to-last/2",
				},
			},
			map[string]interface{}{
				"mountpoint": "/",
				"type":       "measure",
				"prefix":     "leveldb.datastore",
				"child": map[string]interface{}{
					"type":        "levelds",
					"path":        "datastore",
					"compression": "none",
				},
			},
		},
	}
}


/*
// defaultConnMgrHighWater is the default value for the connection managers
// 'high water' mark
const defaultConnMgrHighWater = 40

// defaultConnMgrLowWater is the default value for the connection managers 'low
// water' mark
const defaultConnMgrLowWater = 20

// defaultConnMgrGracePeriod is the default value for the connection managers
// grace period
const defaultConnMgrGracePeriod = time.Second * 60

func addressesConfig() ipfs_config.Addresses {
	return ipfs_config.Addresses{
		Swarm: []string{
			"/ip4/0.0.0.0/tcp/0",
			"/ip6/::/tcp/0",

			"/ip4/0.0.0.0/udp/0/quic",
			"/ip6/::/udp/0/quic",
		},

		// @FIXME: use random port here to avoid collision
		// API:     ipfs_config.Strings{"/ip4/127.0.0.1/tcp/43453"},
		// Gateway: ipfs_config.Strings{"/ip4/127.0.0.1/tcp/43454"},
	}
}

// defaultDatastoreConfig is an internal function exported to aid in testing.
func defaultDatastoreConfig() ipfs_config.Datastore {
	return ipfs_config.Datastore{
		StorageMax:         "500MB",
		StorageGCWatermark: 90, // 90%
		GCPeriod:           "1h",
		BloomFilterSize:    0,
		Spec: map[string]interface{}{
			"type": "mount",
			"mounts": []interface{}{
				map[string]interface{}{
					"mountpoint": "/blocks",
					"type":       "measure",
					"prefix":     "flatfs.datastore",
					"child": map[string]interface{}{
						"type":      "flatfs",
						"path":      "blocks",
						"sync":      true,
						"shardFunc": "/repo/flatfs/shard/v1/next-to-last/2",
					},
				},
				map[string]interface{}{
					"mountpoint": "/",
					"type":       "measure",
					"prefix":     "leveldb.datastore",
					"child": map[string]interface{}{
						"type":        "levelds",
						"path":        "datastore",
						"compression": "none",
					},
				},
			},
		},
	}
	}
	*/


// identityConfig initializes a new identity.
func identityConfig(out io.Writer, nbits int) (ipfs_config.Identity, error) {
	// TODO guard higher up
	log.Tracef("identityConfig() %d", nbits)

	/*
		if conf == nil {
			var err error
			var identity config.Identity
			if nBitsGiven {
				identity, err = config.CreateIdentity(os.Stdout, []options.KeyGenerateOption{
					options.Key.Size(nBitsForKeypair),
					options.Key.Type(algorithm),
				})
			} else {
				identity, err = config.CreateIdentity(os.Stdout, []options.KeyGenerateOption{
					options.Key.Type(algorithm),
				})
			}
			if err != nil {
				return err
			}
			conf, err = config.InitWithIdentity(identity)
			if err != nil {
				return err
			}
		}
	*/
	//old

	ident, err := ipfs_config.CreateIdentity(os.Stdout, []options.KeyGenerateOption{
		options.Key.Type(options.Ed25519Key),
	})

	if err != nil {
		panic(err)
	}

	/*  ident := ipfs_config.Identity{}
	    if nbits < 2048 {
	      return ident, errors.New("bitsize less than 2048 is considered unsafe")
	    }

	    fmt.Fprintf(out, "generating %v-bit RSA keypair...", nbits)
	    sk, pk, err := libp2p_ci.GenerateKeyPair(libp2p_ci.RSA, nbits)
	    if err != nil {
	      return ident, err
	    }
	    fmt.Fprintf(out, "done\n")

	    // currently storing key unencrypted. in the future we need to encrypt it.
	    // TODO(security)
	    skbytes, err := sk.Raw()
	    if err != nil {
	      return ident, err
	    }
	    ident.PrivKey = base64.StdEncoding.EncodeToString(skbytes)

	    id, err := libp2p_peer.IDFromPublicKey(pk)
	    if err != nil {
	      return ident, err
	    }
	    ident.PeerID = id.Pretty()
	    fmt.Fprintf(out, "libp2p_peer identity: %s\n", ident.PeerID)*/
	return ident, nil
}