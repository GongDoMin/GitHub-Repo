package com.prac.network

import com.prac.network.model.RepoDetailDto
import com.prac.network.model.RepoDto

interface RepoApiDataSource {
    suspend fun getRepositories(userName: String, perPage:Int, page: Int) : List<RepoDto>

    suspend fun getRepository(userName: String, repoName: String) : RepoDetailDto

    suspend fun getRepoIssueCount(userName: String, repoName: String) : Int

    suspend fun getRepoPullCount(userName: String, repoName: String) : Int

    suspend fun getRepoReadme(userName: String, repoName: String) : String
}