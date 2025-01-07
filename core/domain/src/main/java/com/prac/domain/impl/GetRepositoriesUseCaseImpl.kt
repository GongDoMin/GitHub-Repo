package com.prac.domain.impl

import androidx.paging.PagingData
import com.prac.data.model.Repository
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.GetRepositoriesUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepositoriesUseCaseImpl @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
): GetRepositoriesUseCase {
    override suspend fun invoke() : Flow<PagingData<Repository>> =
        repoRepository.getRepositories(userRepository.getLocalUserName())
}