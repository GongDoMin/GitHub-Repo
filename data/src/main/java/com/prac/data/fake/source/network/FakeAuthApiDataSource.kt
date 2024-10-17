package com.prac.data.fake.source.network

import com.prac.data.repository.model.TokenModel
import com.prac.data.source.network.AuthApiDataSource
import java.time.ZonedDateTime

internal class FakeAuthApiDataSource(
    private val token: TokenModel
) : AuthApiDataSource {
    private lateinit var throwable: Throwable

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun authorizeOAuth(code: String): TokenModel {
        if (::throwable.isInitialized) throw throwable

        return token
    }

    override suspend fun refreshAccessToken(refreshToken: String): TokenModel {
        if (::throwable.isInitialized) throw throwable

        return TokenModel(
            accessToken = "refreshAccessToken",
            refreshToken = "refreshRefreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now()
        )
    }
}