package com.prac.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.prac.data.model.RepositoryDetail
import com.prac.data.model.Repository
import com.prac.local.model.RepositoryEntity
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
abstract class RepoRepository : RemoteMediator<Int, RepositoryEntity>() {
    abstract suspend fun getRepositories(userName: String) : Flow<PagingData<Repository>>

    abstract suspend fun getRepository(userName: String, repoName: String) : Result<RepositoryDetail>

    abstract suspend fun clearRepositories()

    abstract suspend fun getStarStateAndStarCount(id: Int) : Flow<Pair<Boolean?, Int?>>

    abstract suspend fun isStarred(id: Int, repoName: String)

    abstract suspend fun starRepository(userName: String, repoName: String) : Result<Unit>

    abstract suspend fun unStarRepository(userName: String, repoName: String) : Result<Unit>

    abstract suspend fun starLocalRepository(id: Int, updatedStarCount: Int)

    abstract suspend fun unStarLocalRepository(id: Int, updatedStarCount: Int)
}