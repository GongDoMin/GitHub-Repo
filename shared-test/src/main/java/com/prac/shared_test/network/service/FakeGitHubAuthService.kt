package com.prac.shared_test.network.service

import com.prac.network.model.response.TokenResponse
import com.prac.network.service.GitHubAuthService

class FakeGitHubAuthService(
    private var token: TokenResponse
): GitHubAuthService {

    override suspend fun authorizeOAuth(accept: String, clientID: String, clientSecret: String, code: String): TokenResponse {
        return token
    }

    override suspend fun refreshAccessToken(accept: String, clientID: String, clientSecret: String, grantType: String, refreshToken: String): TokenResponse {
        return TokenResponse(
            accessToken = "refreshAccessToken",
            expiresIn = 3600,
            refreshToken= "refreshRefreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )
    }
}
