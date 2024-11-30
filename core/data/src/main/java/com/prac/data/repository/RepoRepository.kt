package com.prac.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.prac.data.model.RepoDetailModel
import com.prac.data.model.RepoModel
import com.prac.local.room.entity.Repository
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
abstract class RepoRepository : RemoteMediator<Int, Repository>() {
    abstract suspend fun getRepositories() : Flow<PagingData<RepoModel>>

    abstract suspend fun getRepository(userName: String, repoName: String) : Result<RepoDetailModel>

    abstract suspend fun getRepoIssueSize(userName: String, repoName: String) : Result<Int>

    abstract suspend fun getRepoPullSize(userName: String, repoName: String) : Result<Int>

    abstract suspend fun clearRepositories()

    abstract suspend fun getStarStateAndStarCount(id: Int) : Flow<Pair<Boolean?, Int?>>

    abstract suspend fun isStarred(id: Int, repoName: String)

    abstract suspend fun starRepository(userName: String, repoName: String) : Result<Unit>

    abstract suspend fun unStarRepository(userName: String, repoName: String) : Result<Unit>

    abstract suspend fun starLocalRepository(id: Int, updatedStarCount: Int)

    abstract suspend fun unStarLocalRepository(id: Int, updatedStarCount: Int)
}