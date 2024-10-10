package com.prac.data.source.network

import com.prac.data.source.network.dto.TokenDto
import com.prac.data.source.network.impl.AuthApiDataSourceImpl
import com.prac.data.source.network.service.GitHubAuthService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class AuthApiDataSourceTest {

    @Mock private lateinit var gitHubAuthService: GitHubAuthService
    private lateinit var authApiDataSource: AuthApiDataSource

    @Before
    fun setup() {
        authApiDataSource = AuthApiDataSourceImpl(gitHubAuthService)
    }

    @Test
    fun authorizeOAuth_tokenPassedToDataSource() = runTest {
        val code = "code"
        val tokenDto = TokenDto("accessToken", 3600, "refreshToken", 3600, "Bearer")
        whenever(gitHubAuthService.authorizeOAuth(code = code)).thenReturn(tokenDto)

        val result = authApiDataSource.authorizeOAuth(code)

        assertEquals(result.accessToken, tokenDto.accessToken)
        assertEquals(result.refreshToken, tokenDto.refreshToken)
        assertEquals(result.expiresInSeconds, tokenDto.expiresIn)
        assertEquals(result.refreshTokenExpiresInSeconds, tokenDto.refreshTokenExpiresIn)
    }
}