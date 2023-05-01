package main

import (
	kubo_repo "github.com/ipfs/kubo/repo"
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
