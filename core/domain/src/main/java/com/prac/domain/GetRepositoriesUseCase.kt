package com.prac.domain

import androidx.paging.PagingData
import com.prac.data.model.RepoModel
import com.prac.domain.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

interface GetRepositoriesUseCase {
    suspend operator fun invoke() : Flow<PagingData<RepoEntity>>
}