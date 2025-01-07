package com.prac.domain

import androidx.paging.PagingData
import com.prac.data.model.Repository
import kotlinx.coroutines.flow.Flow

interface GetRepositoriesUseCase {
    suspend operator fun invoke() : Flow<PagingData<Repository>>
}