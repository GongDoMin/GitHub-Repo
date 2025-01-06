package com.prac.domain.impl

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.AuthorizeOAuthUseCase
import java.io.IOException
import javax.inject.Inject

class AuthorizeOAuthUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) : AuthorizeOAuthUseCase {
    override suspend fun invoke(code: String) : Result<Unit> {
        return try {
            val accessToken = tokenRepository.authorizeOAuth(code)

            val userName = userRepository.getUserName(accessToken)
            userRepository.setUserName(userName)

            Result.success(Unit)
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    Result.failure(CommonException.NetworkError())
                }
                else -> {
                    Result.failure(CommonException.AuthorizationError())
                }
            }
        }
    }
}