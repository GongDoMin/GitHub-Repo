package com.prac.data.fake.repository

import com.prac.data.exception.CommonException
import com.prac.data.repository.TokenRepository
import com.prac.data.source.local.datastore.TokenLocalDto
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class FakeTokenRepository: TokenRepository {

    private lateinit var throwable: Throwable

    private var token: TokenLocalDto = makeEmptyToken()

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    fun setInitialToken() {
        token = makeNewToken()
    }

    override suspend fun authorizeOAuth(code: String): Result<Unit> {
        if (::throwable.isInitialized) {
            return when(throwable) {
                is IOException -> Result.failure(CommonException.NetworkError())
                else -> Result.failure(CommonException.AuthorizationError())
            }
        }

        return Result.success(Unit)
    }

    override suspend fun isLoggedIn(): Boolean {
        return token.accessToken.isNotEmpty()
    }

    override suspend fun clearToken() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(refreshToken: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getAccessToken(): String {
        TODO("Not yet implemented")
    }

    override fun getRefreshToken(): String {
        TODO("Not yet implemented")
    }

    override fun getAccessTokenIsExpired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRefreshTokenIsExpired(): Boolean {
        TODO("Not yet implemented")
    }

    private fun makeEmptyToken() =
        TokenLocalDto(
            accessToken = "",
            refreshToken = "",
            expiresInSeconds = 0,
            refreshTokenExpiresInSeconds = 0,
            updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
        )

    private fun makeNewToken() =
        TokenLocalDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 18000,
            updatedAt = Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli()).atZone(ZoneId.systemDefault())
        )
}