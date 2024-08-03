package com.prac.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.prac.data.entity.RepoEntity
import com.prac.data.repository.RepoRepository
import com.prac.data.source.RepoApiDataSource
import com.prac.data.source.impl.RepoApiDataSourceImpl.Companion.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RepoRepositoryImpl @Inject constructor(
    private val repoApiDataSource: RepoApiDataSource
) : RepoRepository {
    override suspend fun getRepositories(): Flow<PagingData<RepoEntity>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { repoApiDataSource }
        ).flow
            .map { pagingData ->
                pagingData.map { repoModel ->
                    repoModel.toEntity()
                }
            }
}

