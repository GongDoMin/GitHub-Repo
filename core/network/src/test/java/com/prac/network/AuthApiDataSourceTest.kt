package com.prac.network

import com.prac.network.model.TokenDto
import com.prac.network.impl.AuthApiDataSourceImpl
import com.prac.shared_test.network.service.FakeGitHubAuthService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

internal class AuthApiDataSourceTest {

    private lateinit var gitHubAuthService: FakeGitHubAuthService
    private lateinit var authApiDataSource: AuthApiDataSource

    private val token = TokenDto(
        accessToken = "accessToken",
        expiresIn = 3600,
        refreshToken= "refreshToken",
        refreshTokenExpiresIn = 18000,
        scope = "",
        tokenType = "Bearer"
    )
    private val code = "code"

    @Before
    fun setup() {
        gitHubAuthService = FakeGitHubAuthService(token)
        authApiDataSource = AuthApiDataSourceImpl(gitHubAuthService)
    }

    @Test
    fun authorizeOAuth_whenCalled_token() = runTest {

        val result = authApiDataSource.authorizeOAuth(code)

        assertEquals(result.accessToken, token.accessToken)
        assertEquals(result.refreshToken, token.refreshToken)
        assertEquals(result.expiresIn, token.expiresIn)
        assertEquals(result.refreshTokenExpiresIn, token.refreshTokenExpiresIn)
    }

    @Test
    fun refreshAccessToken_whenCalled_refreshToken() = runTest {

        val result = authApiDataSource.refreshAccessToken(code)

        assertNotEquals(result.accessToken, token.accessToken)
        assertNotEquals(result.refreshToken, token.refreshToken)
    }
}