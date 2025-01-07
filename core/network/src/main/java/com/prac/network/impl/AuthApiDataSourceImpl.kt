package com.prac.network.impl

import com.prac.network.AuthApiDataSource
import com.prac.network.model.response.TokenResponse
import com.prac.network.service.GitHubAuthService
import javax.inject.Inject

internal class AuthApiDataSourceImpl @Inject constructor(
    private val gitHubAuthService: GitHubAuthService
) : AuthApiDataSource {
    override suspend fun authorizeOAuth(code: String): TokenResponse {
        val response = gitHubAuthService.authorizeOAuth(code = code)

        return response
    }

    override suspend fun refreshAccessToken(refreshToken: String): TokenResponse {
        val response = gitHubAuthService.refreshAccessToken(refreshToken = refreshToken)

        return response
    }
}