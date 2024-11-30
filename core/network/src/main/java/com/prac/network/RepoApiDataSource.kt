package com.prac.network

import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto

interface RepoApiDataSource {
    suspend fun getRepositories(userName: String, perPage:Int, page: Int) : List<RepoDto>

    suspend fun getRepository(userName: String, repoName: String) : RepoDetailDto

    suspend fun getRepoIssueSize(userName: String, repoName: String) : Int

    suspend fun getRepoPullSize(userName: String, repoName: String) : Int
}