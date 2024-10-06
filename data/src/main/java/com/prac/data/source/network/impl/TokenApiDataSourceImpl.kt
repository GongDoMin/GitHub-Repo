package com.prac.data.source.network.impl

import com.prac.data.repository.model.TokenModel
import com.prac.data.source.network.TokenApiDataSource
import com.prac.data.source.network.service.GitHubAuthService
import java.time.ZonedDateTime
import javax.inject.Inject

internal class TokenApiDataSourceImpl @Inject constructor(
    private val gitHubAuthService: GitHubAuthService
) : TokenApiDataSource {
    override suspend fun authorizeOAuth(code: String): TokenModel {
        val response = gitHubAuthService.authorizeOAuth(code = code)

        return TokenModel(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresInSeconds = response.expiresIn,
            refreshTokenExpiresInSeconds = response.refreshTokenExpiresIn,
            updatedAt = ZonedDateTime.now()
        )
    }

    override suspend fun refreshAccessToken(refreshToken: String): TokenModel {
        val response = gitHubAuthService.refreshAccessToken(refreshToken = refreshToken)

        return TokenModel(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresInSeconds = response.expiresIn,
            refreshTokenExpiresInSeconds = response.refreshTokenExpiresIn,
            updatedAt = ZonedDateTime.now()
        )
    }
}