package com.prac.network

import com.prac.network.model.response.RepositoryDetailResponse
import com.prac.network.model.response.RepositoryResponse

interface RepoApiDataSource {
    suspend fun getRepositories(userName: String, perPage:Int, page: Int) : List<RepositoryResponse>

    suspend fun getRepository(userName: String, repoName: String) : RepositoryDetailResponse

    suspend fun getRepoIssueCount(userName: String, repoName: String) : Int

    suspend fun getRepoPullCount(userName: String, repoName: String) : Int

    suspend fun getRepoReadme(userName: String, repoName: String) : String
}