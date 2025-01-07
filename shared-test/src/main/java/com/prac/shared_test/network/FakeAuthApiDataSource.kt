package com.prac.shared_test.network

import com.prac.network.AuthApiDataSource
import com.prac.network.model.response.TokenResponse

class FakeAuthApiDataSource: AuthApiDataSource {

    private lateinit var throwable: Throwable

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun authorizeOAuth(code: String): TokenResponse {
        if (::throwable.isInitialized) throw throwable

        return TokenResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresIn = 3600,
            refreshTokenExpiresIn = 3600,
            scope = "",
            tokenType = "Bearer"
        )
    }

    override suspend fun refreshAccessToken(refreshToken: String): TokenResponse {
        if (::throwable.isInitialized) throw throwable

        return TokenResponse(
            accessToken = "refreshAccessToken",
            refreshToken = "refreshRefreshToken",
            expiresIn = 3600,
            refreshTokenExpiresIn = 3600,
            scope = "",
            tokenType = "Bearer"
        )
    }
}