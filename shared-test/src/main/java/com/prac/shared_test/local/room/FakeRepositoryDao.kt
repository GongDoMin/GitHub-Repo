package com.prac.shared_test.local.room

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.model.RepositoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepositoryDao : RepositoryDao {

    private val repos = mutableListOf<RepositoryEntity>()

    override fun getRepositories(): PagingSource<Int, RepositoryEntity> {
        return object : PagingSource<Int, RepositoryEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepositoryEntity> {
                val key = params.key ?: 1
                val pageSize = params.loadSize
                val startIndex = (key - 1) * pageSize
                val endIndex = minOf(startIndex + pageSize, repos.size)

                return LoadResult.Page(
                    data = repos.subList(startIndex, endIndex),
                    prevKey = if (key == 1) null else key - 1,
                    nextKey = if (endIndex < repos.size) null else key + 1
                )
            }

            override fun getRefreshKey(state: PagingState<Int, RepositoryEntity>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }

    override fun getRepository(id: Int): Flow<RepositoryEntity?> {
        return flow { emit(repos.find { it.id == id }) }
    }

    override suspend fun insertRepositories(repos: List<RepositoryEntity>) {
        this.repos.addAll(repos)
    }

    override suspend fun updateStarStateAndStarCount(id: Int, isStarred: Boolean, updatedCount: Int) {
        val index = repos.indexOfFirst { it.id == id }
        if (index != -1) {
            repos[index] = repos[index].copy(isStarred = isStarred, stargazersCount = updatedCount)
        }
    }

    override suspend fun updateStarState(id: Int, isStarred: Boolean) {
        val index = repos.indexOfFirst { it.id == id }
        if (index != -1) {
            repos[index] = repos[index].copy(isStarred = isStarred)
        }
    }

    override suspend fun updateStarCount(id: Int, updatedCount: Int) {
        val index = repos.indexOfFirst { it.id == id }
        if (index != -1) {
            repos[index] = repos[index].copy(stargazersCount = updatedCount)
        }
    }

    override suspend fun clearRepositories() {
        repos.clear()
    }
}