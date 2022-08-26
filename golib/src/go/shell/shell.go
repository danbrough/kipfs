package shell

import (
	"bytes"
	"context"

	"github.com/danbrough/kipfs/misc"
	ipfsapi "github.com/ipfs/go-ipfs-api"
	files "github.com/ipfs/go-ipfs-files"
	log "github.com/sirupsen/logrus"

	"io"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
)

func init() {
	println("shell.init()")
}

type Shell struct {
	ishell *ipfsapi.Shell
	url    string
}

func NewShell(url string) *Shell {
	return &Shell{
		ishell: ipfsapi.NewShell(url),
		url:    url,
	}
}

func NewLocalShell() *Shell {
	return &Shell{
		ishell: ipfsapi.NewLocalShell(),
		url:    "",
	}
}

func (s *Shell) DagPut(data string) string {
	put, err := s.ishell.DagPut(data, "dag-json", "dag-cbor")
	if err != nil {
		log.Error(err)
		return ""
	}
	return put
}

func (s *Shell) WriteStuff(data []byte, path string) {

	err := s.ishell.FilesWrite(context.Background(), path, bytes.NewReader(data),
		ipfsapi.FilesWrite.Create(true),
		ipfsapi.FilesWrite.Truncate(true),
		ipfsapi.FilesWrite.Parents(true))

	if err != nil {
		log.Errorf("Failed to write stuff: %s", err.Error())
	}
}

func (s *Shell) AddDir(path string) (string, error) {
	return s.ishell.AddDir(path)
}

/**
Fileswrite

	fr := files.NewReaderFile(data)
	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry("", fr)})
	fileReader := files.NewMultiFileReader(slf, true)


AddDir
	sf, err := files.NewSerialFile(dir, false, stat)
	if err != nil {
		return "", err
	}
	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry(filepath.Base(dir), sf)})
	reader := files.NewMultiFileReader(slf, true)


DagPut
	fr := files.NewReaderFile(r)
	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry("", fr)})
	fileReader := files.NewMultiFileReader(slf, true)

*/
func (s *Shell) NewRequest(command string) *RequestBuilder {
	return &RequestBuilder{
		rb: s.ishell.Request(strings.TrimLeft(command, "/")),
	}
}

type RequestBuilder struct {
	rb *ipfsapi.RequestBuilder
}

type Response struct {
	res *ipfsapi.Response
}

func (res *Response) Close() error {
	return res.res.Close()
}

func (res *Response) Read(p []byte) (n int, err error) {
	n, err = res.res.Output.Read(p)
	if err == io.EOF {
		err = nil
		n = -1
	}
	return n, err
}

func (req *RequestBuilder) Send() ([]byte, error) {
	res, err := req.rb.Send(context.Background())
	if err != nil {
		return nil, err
	}

	var doClose = func() {
		// testing.TestLog.Warn("Closing res")
		err := res.Close()
		if err != nil {
			log.Errorf("Error closing res: %s", err)
		}
	}

	defer doClose()

	if res.Error != nil {
		return nil, res.Error
	}

	return ioutil.ReadAll(res.Output)
}


func (req *RequestBuilder) Argument(arg string) {
	req.rb.Arguments(arg)
}

func (req *RequestBuilder) BoolOptions(key string, value bool) {
	req.rb.Option(key, value)
}

func (req *RequestBuilder) ByteOptions(key string, value []byte) {
	req.rb.Option(key, value)
}

func (req *RequestBuilder) StringOptions(key string, value string) {
	req.rb.Option(key, value)
}

func (req *RequestBuilder) Header(name, value string) {
	req.rb.Header(name, value)
}

func (req *RequestBuilder) BodyString(body string) {
	req.rb.BodyString(body)
}

func (req *RequestBuilder) BodyBytes(body []byte) {
	req.rb.BodyBytes(body)
}

func (req *RequestBuilder) PostData(data []byte, callback misc.Callback) {
	req.post("", files.NewReaderFile(bytes.NewReader(data)), callback)
}

func (req *RequestBuilder) PostData2(data []byte) ([]byte, error) {
	return req.Post2("", files.NewReaderFile(bytes.NewReader(data)))
}

func (req *RequestBuilder) PostData3(data []byte) (*Response, error) {
	return req.post3("", files.NewReaderFile(bytes.NewReader(data)))
}

func (req *RequestBuilder) PostString(data string, callback misc.Callback) {
	req.post("", files.NewReaderFile(strings.NewReader(data)), callback)
}

func (req *RequestBuilder) PostString2(data string) ([]byte, error) {
	return req.Post2("", files.NewReaderFile(strings.NewReader(data)))
}

func (req *RequestBuilder) PostString3(data string) (*Response, error) {
	return req.post3("", files.NewReaderFile(strings.NewReader(data)))
}

// func (req *RequestBuilder) PostString3(data string) (*Response, error) {
// 	fr := files.NewReaderFile(strings.NewReader(data))
// 	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry("", fr)})
// 	fileReader := files.NewMultiFileReader(slf, true)
// 	rb.Body(fileReader)

// }

func (req *RequestBuilder) PostReader(name string, data misc.KReader, callback misc.Callback) {
	req.post(name, files.NewReaderFile(data), callback)
}

func (req *RequestBuilder) PostDirectory(dir string, callback misc.Callback) {
	stat, err := os.Lstat(dir)
	if err != nil {
		callback.OnError(err.Error())
		return
	}

	sf, err := files.NewSerialFile(dir, false, stat)
	if err != nil {
		callback.OnError(err.Error())
		return
	}
	//slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry(filepath.Base(dir), sf)})
	req.post(filepath.Base(dir), sf, callback)
}

func (req *RequestBuilder) post(name string, file files.Node, callback misc.Callback) {
	/*
	   var r io.Reader
	   switch data := data.(type) {
	   case string:
	     r = strings.NewReader(data)
	   case []byte:
	     println("data is byte[] of length", len(data))
	     r = bytes.NewReader(data)
	   case []int8:
	     println("in8 array length: ", len(data))
	     r = NewTestReader(data)

	   case io.Reader:
	     r = data
	   }
	*/
	//fr := files.NewReaderFile(data)
	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry(name, file)})
	fileReader := files.NewMultiFileReader(slf, true)
	req.rb.Body(fileReader)
	res, err := req.rb.Send(context.Background())
	if err != nil {
		log.Errorf("Error: %s", err.Error())
		callback.OnError(err.Error())
	} else {
		respData, _ := ioutil.ReadAll(res.Output)
		//testing.TestLog.Info("Response: %s", string(respData))
		callback.OnResponse(respData)
	}

	//testing.TestLog.Debug("queued response")
}

func (req *RequestBuilder) Post2(name string, file files.Node) ([]byte, error) {
	/*
	   var r io.Reader
	   switch data := data.(type) {
	   case string:
	     r = strings.NewReader(data)
	   case []byte:
	     println("data is byte[] of length", len(data))
	     r = bytes.NewReader(data)
	   case []int8:
	     println("in8 array length: ", len(data))
	     r = NewTestReader(data)

	   case io.Reader:
	     r = data
	   }
	*/
	//fr := files.NewReaderFile(data)


	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry(name, file)})
	fileReader := files.NewMultiFileReader(slf, true)
	req.rb.Body(fileReader)
	res, err := req.rb.Send(context.Background())
	if err != nil {
		log.Errorf("Error: %s", err.Error())
		return nil, err
	} else {
		respData, err := ioutil.ReadAll(res.Output)
		//testing.TestLog.Info("Response: %s", string(respData))
		if err != nil {
			return nil, err
		}
		return respData, nil
	}

	//testing.TestLog.Debug("queued response")
}

func (req *RequestBuilder) post3(name string, file files.Node) (*Response, error) {
	/*
	   var r io.Reader
	   switch data := data.(type) {
	   case string:
	     r = strings.NewReader(data)
	   case []byte:
	     println("data is byte[] of length", len(data))
	     r = bytes.NewReader(data)
	   case []int8:
	     println("in8 array length: ", len(data))
	     r = NewTestReader(data)

	   case io.Reader:
	     r = data
	   }
	*/
	//fr := files.NewReaderFile(data)
	slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry(name, file)})
	fileReader := files.NewMultiFileReader(slf, true)
	req.rb.Body(fileReader)
	res, err := req.rb.Send(context.Background())
	if err != nil {
		log.Errorf("Error: %s", err.Error())
		return nil, err
	} else {
		return &Response{res: res}, nil
	}

	//testing.TestLog.Debug("queued response")
}

func (req *RequestBuilder) Post4(body misc.KReader) (*Response, error) {
	/*
	   var r io.Reader
	   switch data := data.(type) {
	   case string:
	     r = strings.NewReader(data)
	   case []byte:
	     println("data is byte[] of length", len(data))
	     r = bytes.NewReader(data)
	   case []int8:
	     println("in8 array length: ", len(data))
	     r = NewTestReader(data)

	   case io.Reader:
	     r = data
	   }
	*/
	//fr := files.NewReaderFile(data)
	/*slf := files.NewSliceDirectory([]files.DirEntry{files.FileEntry(name, file)})
	  fileReader := files.NewMultiFileReader(slf, true)*/
	req.rb.Body(body)
	res, err := req.rb.Send(context.Background())
	if err != nil {
		log.Errorf("Error: %s", err.Error())
		return nil, err
	} else {
		return &Response{res: res}, nil
	}

	//testing.TestLog.Debug("queued response")
}

// Helpers

// NewUDSShell New unix socket domain shell
func NewUDSShell(sockpath string) *Shell {
	return NewShell("/unix/" + sockpath)
}

func NewTCPShell(port string) *Shell {
	return NewShell("/ip4/127.0.0.1/tcp/" + port)
}

type DataCallback interface {
	OnResponse(data []byte, err *string)
}

type DataCallbackImpl struct {
}

func (c DataCallbackImpl) OnResponse(data []byte, err *string) {
	if err != nil {
		println("Error:", err)
		return
	}
	println("OnResponse() ", string(data))
}
