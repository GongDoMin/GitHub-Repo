package com.prac.data.source.network.impl

import com.prac.data.repository.model.TokenModel
import com.prac.data.source.network.TokenApiDataSource
import com.prac.data.source.network.service.GitHubAuthService
import java.time.ZonedDateTime
import javax.inject.Inject

internal class TokenApiDataSourceImpl @Inject constructor(
    private val gitHubAuthService: GitHubAuthService
) : TokenApiDataSource {
    override suspend fun getToken(
        code: String
    ): TokenModel {
        val response = gitHubAuthService.getAccessTokenApi(
            code = code
        )

        return TokenModel(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresInSeconds = response.expiresIn,
            refreshTokenExpiresInSeconds = response.refreshTokenExpiresIn,
            updatedAt = ZonedDateTime.now()
        )
    }

    override suspend fun refreshToken(refreshToken: String): TokenModel {
        val response = gitHubAuthService.refreshToken(refreshToken = refreshToken)

        return TokenModel(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresInSeconds = response.expiresIn,
            refreshTokenExpiresInSeconds = response.refreshTokenExpiresIn,
            updatedAt = ZonedDateTime.now()
        )
    }
}