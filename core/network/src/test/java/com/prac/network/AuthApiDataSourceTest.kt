package com.prac.network

import com.prac.network.model.response.TokenResponse
import com.prac.network.impl.AuthApiDataSourceImpl
import com.prac.network.service.GitHubAuthService
import com.prac.shared_test.network.service.FakeGitHubAuthService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

internal class AuthApiDataSourceTest {

    private val gitHubAuthService: GitHubAuthService = FakeGitHubAuthService()
    private val authApiDataSource: AuthApiDataSource = AuthApiDataSourceImpl(gitHubAuthService)

    @Test
    fun 인증_요청시_토큰_반환() = runTest {
        // given
        val expectedToken = TokenResponse(
            accessToken = "accessToken",
            expiresIn = 3600,
            refreshToken = "refreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )

        // when
        val result = authApiDataSource.authorizeOAuth("code")

        // then
        assertEquals(expectedToken, result)
    }

    @Test
    fun 리플레시토큰_요청시_토큰_반환() = runTest {
        // given
        val expectedToken = TokenResponse(
            accessToken = "refreshAccessToken",
            expiresIn = 3600,
            refreshToken = "refreshRefreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )

        // when
        val result = authApiDataSource.refreshAccessToken("code")

        // then
        assertEquals(expectedToken, result)
    }
}