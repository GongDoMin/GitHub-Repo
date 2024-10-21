package com.prac.network

interface RepoStarApiDataSource {
    suspend fun isStarred(userName: String, repoName: String)

    suspend fun starRepository(userName: String, repoName: String)

    suspend fun unStarRepository(userName: String, repoName: String)
}