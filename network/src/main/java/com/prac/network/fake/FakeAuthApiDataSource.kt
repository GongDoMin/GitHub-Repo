package com.prac.network.fake

import com.prac.network.AuthApiDataSource
import com.prac.network.dto.TokenDto

class FakeAuthApiDataSource(
    private val token: TokenDto
) : AuthApiDataSource {
    private lateinit var throwable: Throwable

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun authorizeOAuth(code: String): TokenDto {
        if (::throwable.isInitialized) throw throwable

        return token
    }

    override suspend fun refreshAccessToken(refreshToken: String): TokenDto {
        if (::throwable.isInitialized) throw throwable

        return TokenDto(
            accessToken = "refreshAccessToken",
            refreshToken = "refreshRefreshToken",
            expiresIn = 3600,
            refreshTokenExpiresIn = 3600,
            scope = "",
            tokenType = "Bearer"
        )
    }
}