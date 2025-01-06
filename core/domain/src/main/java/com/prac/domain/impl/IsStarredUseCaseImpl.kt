package com.prac.domain.impl

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.IsStarredUseCase
import javax.inject.Inject

class IsStarredUseCaseImpl @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
) : IsStarredUseCase {
    override suspend fun invoke() {
        TODO("Not yet implemented")
    }
}