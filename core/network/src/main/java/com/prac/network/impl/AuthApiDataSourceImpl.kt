package com.prac.network.impl

import com.prac.network.AuthApiDataSource
import com.prac.network.dto.TokenDto
import com.prac.network.service.GitHubAuthService
import javax.inject.Inject

internal class AuthApiDataSourceImpl @Inject constructor(
    private val gitHubAuthService: GitHubAuthService
) : AuthApiDataSource {
    override suspend fun authorizeOAuth(code: String): TokenDto {
        val response = gitHubAuthService.authorizeOAuth(code = code)

        return response
    }

    override suspend fun refreshAccessToken(refreshToken: String): TokenDto {
        val response = gitHubAuthService.refreshAccessToken(refreshToken = refreshToken)

        return response
    }
}