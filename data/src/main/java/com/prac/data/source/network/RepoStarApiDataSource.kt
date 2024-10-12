package com.prac.data.source.network

internal interface RepoStarApiDataSource {
    suspend fun checkRepositoryIsStarred(repoName: String)

    suspend fun starRepository(userName: String, repoName: String)

    suspend fun unStarRepository(userName: String, repoName: String)
}