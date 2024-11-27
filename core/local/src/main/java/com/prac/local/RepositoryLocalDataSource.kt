package com.prac.local

import androidx.paging.PagingSource
import com.prac.local.room.entity.Repository
import kotlinx.coroutines.flow.Flow

interface RepositoryLocalDataSource {
    fun getRepositories(): PagingSource<Int, Repository>

    fun getRepository(id: Int): Flow<Repository?>

    suspend fun insertRepositories(repos: List<Repository>)

    suspend fun updateStarStateAndStarCount(id: Int, isStarred: Boolean, updatedCount: Int)

    suspend fun updateStarState(id: Int, isStarred: Boolean)

    suspend fun updateStarCount(id: Int, updatedCount: Int)

    suspend fun clearRepositories()
}