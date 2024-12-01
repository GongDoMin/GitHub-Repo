package com.prac.network.impl

import com.prac.network.RepoApiDataSource
import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto
import com.prac.network.service.GitHubService
import okio.IOException
import retrofit2.HttpException
import java.util.Base64
import javax.inject.Inject

internal class RepoApiDataSourceImpl @Inject constructor(
    private val gitHubService: GitHubService
) : RepoApiDataSource {
    override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepoDto> {
        val response = gitHubService.getRepos(userName, perPage, page)

        return response
    }

    override suspend fun getRepository(userName: String, repoName: String): RepoDetailDto {
        val response = gitHubService.getRepo(userName, repoName)

        return response
    }

    override suspend fun getRepoIssueCount(userName: String, repoName: String): Int {
        val issueSize = gitHubService.getRepoIssues(userName, repoName).size

        return issueSize
    }

    override suspend fun getRepoPullCount(userName: String, repoName: String): Int {
        val pullSize = gitHubService.getRepoPulls(userName, repoName).size

        return pullSize
    }

    override suspend fun getRepoReadme(userName: String, repoName: String): String {
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