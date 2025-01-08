package com.prac.shared_test.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.model.RepositoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepositoryLocalDataSource : RepositoryLocalDataSource {

    private val repositories = mutableListOf<RepositoryEntity>()

    override fun getRepositories(): PagingSource<Int, RepositoryEntity> {
        return object : PagingSource<Int, RepositoryEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepositoryEntity> {
                val key = params.key ?: 1
                val pageSize = params.loadSize
                val startIndex = (key - 1) * pageSize
                val endIndex = minOf(startIndex + pageSize, repositories.size)

                return LoadResult.Page(
                    data = repositories.subList(startIndex, endIndex),
                    prevKey = if (key == 1) null else key - 1,
                    nextKey = if (endIndex < repositories.size) null else key + 1
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
        return flow { emit(repositories.find { it.id == id }) }
    }

    override suspend fun insertRepositories(repos: List<RepositoryEntity>) {
        this.repositories.addAll(repos)
    }

    override suspend fun updateStarStateAndStarCount(id: Int, isStarred: Boolean, updatedCount: Int) {
        val index = repositories.indexOfFirst { it.id == id }
        if (index != -1) {
            repositories[index] = repositories[index].copy(isStarred = isStarred, stargazersCount = updatedCount)
        }
    }

    override suspend fun updateStarState(id: Int, isStarred: Boolean) {
        val index = repositories.indexOfFirst { it.id == id }
        if (index != -1) {
            repositories[index] = repositories[index].copy(isStarred = isStarred)
        }
    }

    override suspend fun updateStarCount(id: Int, updatedCount: Int) {
        val index = repositories.indexOfFirst { it.id == id }
        if (index != -1) {
            repositories[index] = repositories[index].copy(stargazersCount = updatedCount)
        }
    }

    override suspend fun clearRepositories() {
        repositories.clear()
    }
}