package com.prac.network

import com.prac.network.model.response.RepoDetailResponse
import com.prac.network.model.response.RepoResponse

interface RepoApiDataSource {
    suspend fun getRepositories(userName: String, perPage:Int, page: Int) : List<RepoResponse>

    suspend fun getRepository(userName: String, repoName: String) : RepoDetailResponse

    suspend fun getRepoIssueCount(userName: String, repoName: String) : Int

    suspend fun getRepoPullCount(userName: String, repoName: String) : Int

    suspend fun getRepoReadme(userName: String, repoName: String) : String
}