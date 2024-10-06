package com.prac.data.repository.impl

import com.prac.data.repository.TokenRepository
import com.prac.data.repository.model.TokenModel
import com.prac.data.source.network.TokenApiDataSource
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.datastore.TokenLocalDto
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val tokenApiDataSource: TokenApiDataSource
) : TokenRepository {
    override suspend fun getTokenApi(code: String): Result<Unit> {
        try {
            val model = tokenApiDataSource.authorizeOAuth(code)
            setToken(model)

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return getAccessToken().isNotEmpty()
    }

    override suspend fun clearToken() {
        tokenLocalDataSource.clearToken()
    }

    override suspend fun refreshToken(refreshToken: String): Result<Unit> {
        return try {
            val model = tokenApiDataSource.refreshAccessToken(refreshToken)
            setToken(model)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAccessToken(): String {
        return tokenLocalDataSource.getToken().accessToken
    }

    override fun getRefreshToken(): String {
        return tokenLocalDataSource.getToken().refreshToken
    }

    override fun getAccessTokenIsExpired(): Boolean {
        return tokenLocalDataSource.getToken().isExpired
    }

    override fun getRefreshTokenIsExpired(): Boolean {
        return tokenLocalDataSource.getToken().isRefreshTokenExpired
    }

    private suspend fun setToken(token: TokenModel) {
        tokenLocalDataSource.setToken(
            TokenLocalDto(
                token.accessToken,
                token.refreshToken,
                token.expiresInSeconds,
                token.refreshTokenExpiresInSeconds,
                token.updatedAt
            )
        )
    }
}