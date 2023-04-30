package main

import (
	kubo_repo "github.com/ipfs/kubo/repo"
	kubo_fsrepo "github.com/ipfs/kubo/repo/fsrepo"
	
)

type RepoMobile struct {
	kubo_repo.Repo
	Path string
}

func NewRepoMobile(path string, repo kubo_repo.Repo) *RepoMobile {
	return &RepoMobile{
		Repo: repo,
		Path: path,
	}
}

func RepoIsInitialized(path string) bool {
	return kubo_fsrepo.IsInitialized(path)
}
