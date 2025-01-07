package com.prac.shared_test.network.service

import com.prac.network.model.TokenDto
import com.prac.network.service.GitHubAuthService

class FakeGitHubAuthService(
    private var token: TokenDto
): GitHubAuthService {

    override suspend fun authorizeOAuth(accept: String, clientID: String, clientSecret: String, code: String): TokenDto {
        return token
    }

    override suspend fun refreshAccessToken(accept: String, clientID: String, clientSecret: String, grantType: String, refreshToken: String): TokenDto {
        return TokenDto(
            accessToken = "refreshAccessToken",
            expiresIn = 3600,
            refreshToken= "refreshRefreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )
    }
}
