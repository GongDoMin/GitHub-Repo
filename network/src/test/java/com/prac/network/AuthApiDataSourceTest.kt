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

    private lateinit var gitHubAuthService: GitHubAuthService
    private lateinit var authApiDataSource: AuthApiDataSource

    private val token = TokenDto("accessToken", 3600, "refreshToken", 3600, "", "Bearer")
    private val code = "code"

    @Before
    fun setup() {
        gitHubAuthService = FakeGitHubAuthService(token)
        authApiDataSource = AuthApiDataSourceImpl(gitHubAuthService)
    }

    @Test
    fun authorizeOAuth_tokenPassedToDataSource() = runTest {

        val result = authApiDataSource.authorizeOAuth(code)

        assertEquals(result.accessToken, token.accessToken)
        assertEquals(result.refreshToken, token.refreshToken)
        assertEquals(result.expiresIn, token.expiresIn)
        assertEquals(result.refreshTokenExpiresIn, token.refreshTokenExpiresIn)
    }

    @Test
    fun refreshAccessToken_tokenPassedToDataSource() = runTest {

        val result = authApiDataSource.refreshAccessToken(code)

        assertNotEquals(result.accessToken, token.accessToken)
        assertNotEquals(result.refreshToken, token.refreshToken)
    }
}