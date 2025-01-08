package com.prac.shared_test.network.service

import com.prac.network.model.response.IssueResponse
import com.prac.network.model.response.PullResponse
import com.prac.network.model.response.ReadmeResponse
import com.prac.network.model.response.RepositoryDetailResponse
import com.prac.network.model.response.RepositoryResponse
import com.prac.network.service.GitHubService
import kotlinx.serialization.json.Json
import java.io.File

class FakeGitHubService : GitHubService {

    private val json: Json = Json {
        ignoreUnknownKeys = true
    }
    private val repositories = File("src/main/assets/repository.json").readText()
    private val repository = File("src/main/assets/repositoryDetail.json").readText()
    private val issue = File("src/main/assets/issue.json").readText()
    private val pull = File("src/main/assets/pull.json").readText()
    private val readme = File("src/main/assets/readme.json").readText()

    override suspend fun getRepos(userName: String, perPage: Int, page: Int): List<RepositoryResponse> =
        json.decodeFromString<List<RepositoryResponse>>(repositories)

    override suspend fun getRepo(userName: String, repoName: String): RepositoryDetailResponse =
        json.decodeFromString<RepositoryDetailResponse>(repository)

    override suspend fun isStarred(userName: String, repoName: String) {
        throw NotImplementedError("this method is not supported in FakeGitHubService")
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        throw NotImplementedError("this method is not supported in FakeGitHubService")
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        throw NotImplementedError("this method is not supported in FakeGitHubService")
    }

    override suspend fun getRepoIssues(userName: String, repoName: String): List<IssueResponse> =
        json.decodeFromString<List<IssueResponse>>(issue)

    override suspend fun getRepoPulls(userName: String, repoName: String): List<PullResponse> =
        json.decodeFromString<List<PullResponse>>(pull)

    override suspend fun getRepoReadme(userName: String, repoName: String): ReadmeResponse =
        json.decodeFromString<ReadmeResponse>(readme)
}
