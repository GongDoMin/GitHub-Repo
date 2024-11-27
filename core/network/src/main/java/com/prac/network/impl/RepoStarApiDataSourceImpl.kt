package com.prac.network.impl

import com.prac.network.RepoStarApiDataSource
import com.prac.network.service.GitHubService
import javax.inject.Inject

internal class RepoStarApiDataSourceImpl @Inject constructor(
    private val gitHubService: GitHubService
): RepoStarApiDataSource {
    override suspend fun isStarred(userName: String, repoName: String) {
        gitHubService.isStarred(userName, repoName)
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        gitHubService.starRepository(userName, repoName)
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        gitHubService.unStarRepository(userName, repoName)
    }
}