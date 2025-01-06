package com.prac.domain.impl

import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.AuthorizeOAuthUseCase
import javax.inject.Inject

class AuthorizeOAuthUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) : AuthorizeOAuthUseCase {
    override suspend fun invoke(code: String) {
        TODO("Not yet implemented")
    }
}