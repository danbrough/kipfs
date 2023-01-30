package cids

import (
	"bytes"
	"github.com/ipfs/go-cid"
	"github.com/ipld/go-ipld-prime/codec/dagcbor"
	"github.com/ipld/go-ipld-prime/codec/dagjson"
	basicnode "github.com/ipld/go-ipld-prime/node/basic"
	mh "github.com/multiformats/go-multihash"
	"strings"
)

var cidPrefix = cid.Prefix{
	Codec:    cid.DagCBOR,
	MhLength: -1,
	MhType:   mh.SHA2_256,
	Version:  1,
}

func JsonToCbor(json string) []byte {
	np := basicnode.Prototype.Any
	nb := np.NewBuilder()
	err := dagjson.Decode(nb, strings.NewReader(json))
	node := nb.Build()

	var buf bytes.Buffer
	err = dagcbor.Encode(node, &buf)
	if err != nil {
		panic(err)
	}
	return buf.Bytes()
}

func DagCid(data string) string {
	np := basicnode.Prototype.Any
	nb := np.NewBuilder()
	err := dagjson.Decode(nb, strings.NewReader(data))
	node := nb.Build()

	var buf bytes.Buffer
	err = dagcbor.Encode(node, &buf)
	if err != nil {
		panic(err)
	}

	c, err2 := cidPrefix.Sum(buf.Bytes())
	if err2 != nil {
		panic(err2)
	}

	return c.String()
}

func DagCidBytes(data []byte, format string) string {
	np := basicnode.Prototype.Any
	nb := np.NewBuilder()
	var err error
	if format == "json" {
		err = dagjson.Decode(nb, bytes.NewReader(data))
	} else if format == "cbor" {
		err = dagcbor.Decode(nb, bytes.NewReader(data))
	} else {
		panic("format should be json or cbor")
	}

	if err != nil {
		panic(err)
	}

	node := nb.Build()

	var buf bytes.Buffer
	err = dagcbor.Encode(node, &buf)
	if err != nil {
		panic(err)
	}

	c, err2 := cidPrefix.Sum(buf.Bytes())
	if err2 != nil {
		panic(err2)
	}

	return c.String()
}
