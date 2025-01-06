package com.prac.domain.impl

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.GetRepositoriesUseCase
import javax.inject.Inject

class GetRepositoriesUseCaseImpl @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
): GetRepositoriesUseCase {
    override suspend fun invoke() {
        TODO("Not yet implemented")
    }
}