package com.prac.domain.impl

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.ClearLocalDataUseCase
import javax.inject.Inject

class ClearLocalDataUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val repoRepository: RepoRepository
) : ClearLocalDataUseCase {
    override suspend fun invoke() {
        tokenRepository.clearToken()
        userRepository.clearUserName()
        repoRepository.clearRepositories()
    }
}