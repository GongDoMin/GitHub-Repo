package com.prac.data.source.network.impl

import com.prac.data.source.network.RepoStarApiDataSource
import com.prac.data.source.network.service.GitHubService
import javax.inject.Inject

internal class RepoStarApiDataSourceImpl @Inject constructor(
    private val gitHubService: GitHubService
): RepoStarApiDataSource {
    override suspend fun checkRepositoryIsStarred(repoName: String) {
        gitHubService.checkRepositoryIsStarred("GongDoMin", repoName)
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        gitHubService.starRepository(userName, repoName)
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        gitHubService.unStarRepository(userName, repoName)
    }
}