package com.prac.shared_test.domain

import androidx.paging.PagingData
import com.prac.domain.GetRepositoriesUseCase
import com.prac.domain.entity.RepoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetRepositoriesUseCase(
    private val repoEntityList: List<RepoEntity> = emptyList()
) : GetRepositoriesUseCase {
    override suspend fun invoke(): Flow<PagingData<RepoEntity>> {
        return flow { PagingData.from(repoEntityList) }
    }
}