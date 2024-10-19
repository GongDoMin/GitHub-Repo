package com.prac.data.source.network

internal interface RepoStarApiDataSource {
    suspend fun isStarred(userName: String, repoName: String)

    suspend fun starRepository(userName: String, repoName: String)

    suspend fun unStarRepository(userName: String, repoName: String)
}