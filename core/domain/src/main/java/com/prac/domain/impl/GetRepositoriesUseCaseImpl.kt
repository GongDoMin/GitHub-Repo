package com.prac.domain.impl

import androidx.paging.PagingData
import androidx.paging.map
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.GetRepositoriesUseCase
import com.prac.domain.entity.RepoEntity
import com.prac.domain.entity.toRepoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRepositoriesUseCaseImpl @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
): GetRepositoriesUseCase {
    override suspend fun invoke() : Flow<PagingData<RepoEntity>> =
        repoRepository.getRepositories(userRepository.getLocalUserName()).map { it.map { it.toRepoEntity() } }
}