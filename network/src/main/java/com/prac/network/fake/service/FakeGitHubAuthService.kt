package com.prac.network.fake.service

import com.prac.network.dto.TokenDto
import com.prac.network.service.GitHubAuthService

internal class FakeGitHubAuthService: GitHubAuthService {
    override suspend fun authorizeOAuth(accept: String, clientID: String, clientSecret: String, code: String): TokenDto {
        return TokenDto(
            accessToken = "accessToken",
            expiresIn = 3600,
            refreshToken= "refreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )
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
