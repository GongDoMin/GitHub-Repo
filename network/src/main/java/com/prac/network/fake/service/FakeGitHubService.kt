package com.prac.network.fake.service

import com.prac.network.dto.OwnerDto
import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto
import com.prac.network.service.GitHubService

internal class FakeGitHubService: GitHubService {

    private val repoList = listOf(
        RepoDto(0, "test1", OwnerDto("test1", "test1"), 0, "test1"),
        RepoDto(1, "test2", OwnerDto("test2", "test2"), 0, "test1"),
        RepoDto(2, "test3", OwnerDto("test3", "test3"), 0, "test1"),
        RepoDto(3, "test4", OwnerDto("test4", "test4"), 0, "test1"),
    )

    override suspend fun getRepos(userName: String, perPage: Int, page: Int): List<RepoDto> {
        return repoList
    }

    override suspend fun getRepo(userName: String, repoName: String): RepoDetailDto {
        val repository = repoList.find {
            it.name == repoName && it.owner.login == userName
        } ?: throw Exception("repository is not found")

        return RepoDetailDto(
            id = repository.id,
            name = repository.name,
            owner = repository.owner,
            stargazersCount = repository.stargazersCount,
            forksCount = 0
        )
    }

    override suspend fun isStarred(userName: String, repoName: String) {
        throw NotImplementedError("this method is not supported in FakeGitHubService")
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        throw NotImplementedError("this method is not supported in FakeGitHubService")
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        throw NotImplementedError("this method is not supported in FakeGitHubService")
    }
}