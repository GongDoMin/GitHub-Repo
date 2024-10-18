package com.prac.data.fake.source.network.service

import com.prac.data.source.network.dto.TokenDto
import com.prac.data.source.network.service.GitHubAuthService
import kotlinx.serialization.SerialName

internal class FakeGitHubAuthService(
    private val token: TokenDto
) : GitHubAuthService {
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