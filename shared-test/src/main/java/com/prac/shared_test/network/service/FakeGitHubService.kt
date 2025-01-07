package com.prac.shared_test.network.service

import com.prac.network.model.response.IssueResponse
import com.prac.network.model.response.PullResponse
import com.prac.network.model.response.ReadmeResponse
import com.prac.network.model.response.RepositoryDetailResponse
import com.prac.network.model.response.RepositoryResponse
import com.prac.network.service.GitHubService

class FakeGitHubService(
    private val repoList: List<RepositoryResponse> = emptyList(),
    private val readMe: String = ""
): GitHubService {

    override suspend fun getRepos(userName: String, perPage: Int, page: Int): List<RepositoryResponse> {
        return repoList
    }

    override suspend fun getRepo(userName: String, repoName: String): RepositoryDetailResponse {
        val repository = repoList.find {
            it.name == repoName && it.owner.login == userName
        } ?: throw Exception("repository is not found")

        return RepositoryDetailResponse(
            id = repository.id,
            name = repository.name,
            owner = repository.owner,
            stargazersCount = repository.stargazersCount,
            forksCount = 0,
            subscribersCount = 0
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

    override suspend fun getRepoIssues(userName: String, repoName: String): List<IssueResponse> {
        return listOf(IssueResponse(), IssueResponse())
    }

    override suspend fun getRepoPulls(userName: String, repoName: String): List<PullResponse> {
        return listOf(PullResponse(), PullResponse())
    }

    override suspend fun getRepoReadme(userName: String, repoName: String): ReadmeResponse {
        return ReadmeResponse(content = readMe)
    }
}