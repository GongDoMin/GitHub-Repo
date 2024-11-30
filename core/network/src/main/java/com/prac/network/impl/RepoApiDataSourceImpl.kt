package com.prac.network.impl

import com.prac.network.RepoApiDataSource
import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto
import com.prac.network.service.GitHubService
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

    override suspend fun getRepoIssueSize(userName: String, repoName: String): Int {
        val issueSize = gitHubService.getRepoIssues(userName, repoName).size

        return issueSize
    }

    override suspend fun getRepoPullSize(userName: String, repoName: String): Int {
        val pullSize = gitHubService.getRepoPulls(userName, repoName).size

        return pullSize
    }

}