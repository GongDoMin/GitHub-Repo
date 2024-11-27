package com.prac.local.impl

import androidx.paging.PagingSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.room.entity.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryLocalDataSourceImpl @Inject constructor(
    private val repositoryDao: RepositoryDao
) : RepositoryLocalDataSource {
    override fun getRepositories(): PagingSource<Int, Repository> =
        repositoryDao.getRepositories()

    override fun getRepository(id: Int): Flow<Repository?> =
        repositoryDao.getRepository(id)

    override suspend fun insertRepositories(repos: List<Repository>) =
        repositoryDao.insertRepositories(repos)

    override suspend fun updateStarStateAndStarCount(id: Int, isStarred: Boolean, updatedCount: Int) =
        repositoryDao.updateStarStateAndStarCount(id, isStarred, updatedCount)

    override suspend fun updateStarState(id: Int, isStarred: Boolean) =
        repositoryDao.updateStarState(id, isStarred)

    override suspend fun updateStarCount(id: Int, updatedCount: Int) =
        repositoryDao.updateStarCount(id, updatedCount)

    override suspend fun clearRepositories() =
        repositoryDao.clearRepositories()

}