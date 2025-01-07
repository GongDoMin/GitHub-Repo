package com.prac.local

import androidx.paging.PagingSource
import com.prac.local.model.RepositoryEntity
import kotlinx.coroutines.flow.Flow

interface RepositoryLocalDataSource {
    fun getRepositories(): PagingSource<Int, RepositoryEntity>

    fun getRepository(id: Int): Flow<RepositoryEntity?>

    suspend fun insertRepositories(repos: List<RepositoryEntity>)

    suspend fun updateStarStateAndStarCount(id: Int, isStarred: Boolean, updatedCount: Int)

    suspend fun updateStarState(id: Int, isStarred: Boolean)

    suspend fun updateStarCount(id: Int, updatedCount: Int)

    suspend fun clearRepositories()
}