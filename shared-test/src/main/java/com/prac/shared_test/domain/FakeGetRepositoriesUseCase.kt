package com.prac.shared_test.domain

import androidx.paging.PagingData
import com.prac.data.model.Repository
import com.prac.domain.GetRepositoriesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetRepositoriesUseCase(
    private val repositories: List<Repository> = emptyList()
) : GetRepositoriesUseCase {
    override suspend fun invoke(): Flow<PagingData<Repository>> {
        return flow { PagingData.from(repositories) }
    }
}