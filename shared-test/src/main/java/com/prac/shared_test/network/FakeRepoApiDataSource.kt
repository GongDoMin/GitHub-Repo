package com.prac.shared_test.network

import com.prac.network.RepoApiDataSource
import com.prac.network.model.response.OwnerResponse
import com.prac.network.model.response.RepositoryDetailResponse
import com.prac.network.model.response.RepositoryResponse

class FakeRepoApiDataSource(
    private val repositories: List<RepositoryResponse> = emptyList(),
    private val starCount: Int? = null,
    private val issueCount: Int = 0,
    private val pullCount: Int = 0,
    private val readme: String = "",
    private val throwable: Throwable? = null
) : RepoApiDataSource {

    override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepositoryResponse> {
        throwable?.let { throw it }

        return repositories
    }

    override suspend fun getRepository(userName: String, repoName: String): RepositoryDetailResponse {
        throwable?.let { throw it }

        val repository = repositories.find { it.owner.login == userName && it.name == repoName } ?: throw Exception("repository is not found")

        return RepositoryDetailResponse(
            id = repository.id,
            name = repository.name,
            owner = OwnerResponse(
                login = repository.owner.login,
                avatarUrl = repository.owner.avatarUrl),
            stargazersCount = starCount ?: repository.stargazersCount,
            forksCount = 0,
            subscribersCount = 0
        )
    }

    override suspend fun getRepoIssueCount(userName: String, repoName: String): Int {
        throwable?.let { throw it }

        return issueCount
    }

    override suspend fun getRepoPullCount(userName: String, repoName: String): Int {
        throwable?.let { throw it }

        return pullCount
    }

    override suspend fun getRepoReadme(userName: String, repoName: String): String {
        throwable?.let { throw it }

        return readme
    }
}
