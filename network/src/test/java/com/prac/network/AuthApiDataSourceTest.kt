package com.prac.network

import com.prac.network.dto.TokenDto
import com.prac.network.fake.service.FakeGitHubAuthService
import com.prac.network.impl.AuthApiDataSourceImpl
import com.prac.network.service.GitHubAuthService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

internal class AuthApiDataSourceTest {

    private lateinit var gitHubAuthService: FakeGitHubAuthService
    private lateinit var authApiDataSource: AuthApiDataSource

    private val code = "code"

    @Before
    fun setup() {
        gitHubAuthService = FakeGitHubAuthService()
        authApiDataSource = AuthApiDataSourceImpl(gitHubAuthService)
    }

    @Test
    fun authorizeOAuth_whenCalled_token() = runTest {
        val expectedToken = TokenDto(
            accessToken = "accessToken",
            expiresIn = 3600,
            refreshToken= "refreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )

        val result = authApiDataSource.authorizeOAuth(code)

        assertEquals(result.accessToken, expectedToken.accessToken)
        assertEquals(result.refreshToken, expectedToken.refreshToken)
        assertEquals(result.expiresIn, expectedToken.expiresIn)
        assertEquals(result.refreshTokenExpiresIn, expectedToken.refreshTokenExpiresIn)
    }

    @Test
    fun refreshAccessToken_whenCalled_refreshToken() = runTest {
        val expectedToken = TokenDto(
            accessToken = "refreshAccessToken",
            expiresIn = 3600,
            refreshToken= "refreshRefreshToken",
            refreshTokenExpiresIn = 18000,
            scope = "",
            tokenType = "Bearer"
        )

        val result = authApiDataSource.refreshAccessToken(code)

        assertEquals(result.accessToken, expectedToken.accessToken)
        assertEquals(result.refreshToken, expectedToken.refreshToken)
        assertEquals(result.expiresIn, expectedToken.expiresIn)
        assertEquals(result.refreshTokenExpiresIn, expectedToken.refreshTokenExpiresIn)
    }
}