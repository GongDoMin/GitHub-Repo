package com.prac.data.repository

import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.data.repository.model.TokenModel
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.datastore.TokenLocalDto
import com.prac.data.source.network.AuthApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.ZonedDateTime

@RunWith(MockitoJUnitRunner::class)
class TokenRepositoryTest {

    @Mock private lateinit var tokenLocalDataSource: TokenLocalDataSource
    @Mock private lateinit var authApiDataSource: AuthApiDataSource

    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setUp() {
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource
        )
    }

    @Test
    fun authorizeOAuth_updateDataStoreAndReturnSuccess() = runTest {
        val code = "testCode"
        val token = TokenModel("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())
        whenever(authApiDataSource.authorizeOAuth(code)).thenReturn(token)

        val result = tokenRepository.authorizeOAuth(code)

        verify(tokenLocalDataSource)
            .setToken(
                TokenLocalDto(
                    token.accessToken,
                    token.refreshToken,
                    token.expiresInSeconds,
                    token.refreshTokenExpiresInSeconds,
                    token.updatedAt
                )
            )
        assertTrue(result.isSuccess)
    }

    @Test
    fun authorizeOAuth_returnFailure() = runTest {
        val code = "testCode"
        val exception = RuntimeException()
        whenever(authApiDataSource.authorizeOAuth(code)).thenThrow(exception)

        val result = tokenRepository.authorizeOAuth(code)

        verify(tokenLocalDataSource, never()).setToken(any())
        assertTrue(result.isFailure)
    }

    @Test
    fun refreshToken_updateDataStoreAndReturnSuccess() = runTest {
        val refreshToken = "RefreshToken"
        val token = TokenModel("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())
        whenever(authApiDataSource.refreshAccessToken(refreshToken)).thenReturn(token)

        val result = tokenRepository.refreshToken(refreshToken)

        verify(tokenLocalDataSource)
            .setToken(
                TokenLocalDto(
                    token.accessToken,
                    token.refreshToken,
                    token.expiresInSeconds,
                    token.refreshTokenExpiresInSeconds,
                    token.updatedAt
                )
            )
        assertTrue(result.isSuccess)
    }

    @Test
    fun refreshToken_returnFailure() = runTest {
        val refreshToken = "refreshToken"
        val exception = RuntimeException()
        whenever(authApiDataSource.refreshAccessToken(refreshToken)).thenThrow(exception)

        val result = tokenRepository.refreshToken(refreshToken)

        verify(tokenLocalDataSource, never()).setToken(any())
        assertTrue(result.isFailure)
    }

    @Test
    fun isLoggedIn_accessTokenExist_returnsTrue() = runTest {
        val token = TokenLocalDto("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())
        whenever(tokenLocalDataSource.getToken()).thenReturn(token)

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertTrue(isLoggedIn)
    }

    @Test
    fun isLoggedIn_accessTokenDoesNotExist_returnsFalse() = runTest {
        val token = TokenLocalDto("", "", 0, 0, ZonedDateTime.now())
        whenever(tokenLocalDataSource.getToken()).thenReturn(token)

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertFalse(isLoggedIn)
    }

    @Test
    fun getAccessToken_returnTokenFromDataSource() = runTest {
        val token = TokenLocalDto("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())
        whenever(tokenLocalDataSource.getToken()).thenReturn(token)

        val accessToken = tokenRepository.getAccessToken()

        assertEquals(token.accessToken, accessToken)
    }

    @Test
    fun getRefreshToken_returnTokenFromDataSource() = runTest {
        val token = TokenLocalDto("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())
        whenever(tokenLocalDataSource.getToken()).thenReturn(token)

        val refreshToken = tokenRepository.getRefreshToken()

        assertEquals(token.refreshToken, refreshToken)
    }
}