package com.prac.shared_test.network.service

import com.prac.network.model.IssueDto
import com.prac.network.model.PullDto
import com.prac.network.model.ReadmeDto
import com.prac.network.model.RepoDetailDto
import com.prac.network.model.RepoDto
import com.prac.network.service.GitHubService

class FakeGitHubService(
    private val repoList: List<RepoDto> = emptyList(),
    private val readMe: String = ""
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

    override suspend fun getRepoIssues(userName: String, repoName: String): List<IssueDto> {
        return listOf(IssueDto(), IssueDto())
    }

    override suspend fun getRepoPulls(userName: String, repoName: String): List<PullDto> {
        return listOf(PullDto(), PullDto())
    }

    override suspend fun getRepoReadme(userName: String, repoName: String): ReadmeDto {
        return ReadmeDto(content = readMe)
    }
}