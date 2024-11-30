package com.prac.shared_test.network.service

import com.prac.network.dto.IssueDto
import com.prac.network.dto.PullDto
import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto
import com.prac.network.service.GitHubService

class FakeGitHubService(
    private val repoList: List<RepoDto>
): GitHubService {

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

    override suspend fun getRepoIssues(userName: String, repoName: String): List<IssueDto> {
        return listOf(IssueDto(), IssueDto())
    }

    override suspend fun getRepoPulls(userName: String, repoName: String): List<PullDto> {
        return listOf(PullDto(), PullDto())
    }
}