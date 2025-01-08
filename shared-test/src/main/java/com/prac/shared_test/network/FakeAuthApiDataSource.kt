package com.prac.shared_test.network

import com.prac.network.AuthApiDataSource
import com.prac.network.model.response.TokenResponse

class FakeAuthApiDataSource(
    private val tokenResponse: TokenResponse = TokenResponse(),
    private val refreshTokenResponse: TokenResponse = TokenResponse()
): AuthApiDataSource {
    override suspend fun authorizeOAuth(code: String): TokenResponse =
        tokenResponse

    override suspend fun refreshAccessToken(refreshToken: String): TokenResponse =
        refreshTokenResponse
}