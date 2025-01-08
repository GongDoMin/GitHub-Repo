package com.prac.network.impl

import com.prac.network.RepoApiDataSource
import com.prac.network.model.response.RepositoryDetailResponse
import com.prac.network.model.response.RepositoryResponse
import com.prac.network.service.GitHubService
import retrofit2.HttpException
import java.util.Base64
import javax.inject.Inject

internal class RepoApiDataSourceImpl @Inject constructor(
    private val gitHubService: GitHubService
) : RepoApiDataSource {
    override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepositoryResponse> =
        gitHubService.getRepos(userName, perPage, page)

    override suspend fun getRepository(userName: String, repoName: String): RepositoryDetailResponse =
        gitHubService.getRepo(userName, repoName)

    override suspend fun getRepoIssueCount(userName: String, repoName: String): Int =
        gitHubService.getRepoIssues(userName, repoName).size

    override suspend fun getRepoPullCount(userName: String, repoName: String): Int =
        gitHubService.getRepoPulls(userName, repoName).size

    override suspend fun getRepoReadme(userName: String, repoName: String): String {
        /*
            Readme 가 존재하지 않을 경우 HttpException 발생
         */
        return try {
            val response = gitHubService.getRepoReadme(userName, repoName)

            val cleanedString = response.content.replace("\n", "").replace(" ", "")

            val decodedBytes = Base64.getDecoder().decode(cleanedString)

            String(decodedBytes)
        } catch (e: HttpException) {
            ""
        }
    }
}