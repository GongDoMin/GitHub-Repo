package com.prac.data.fake.source.network.service

import com.prac.data.source.network.dto.RepoDetailDto
import com.prac.data.source.network.dto.RepoDto
import com.prac.data.source.network.service.GitHubService

internal class FakeGitHubService(
    private val repos: List<RepoDto>
) : GitHubService {
    override suspend fun getRepos(userName: String, perPage: Int, page: Int): List<RepoDto> {
        return repos
    }

    override suspend fun getRepo(userName: String, repoName: String): RepoDetailDto {
        val repository = repos.find {
            it.name == repoName && it.owner.login == userName
        }

        if (repository == null) {
            return RepoDetailDto()
        }

        return RepoDetailDto(
            id = repository.id,
            name = repository.name,
            owner = repository.owner,
            stargazersCount = repository.stargazersCount,
            forksCount = 0
        )
    }

    override suspend fun checkRepositoryIsStarred(userName: String, repoName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        TODO("Not yet implemented")
    }
}