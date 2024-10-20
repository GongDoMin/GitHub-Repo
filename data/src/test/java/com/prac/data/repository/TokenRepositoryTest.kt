package com.prac.data.repository

import com.prac.data.exception.CommonException
import com.prac.local.fake.source.FakeTokenLocalDataSource
import com.prac.local.fake.source.FakeUserLocalDataSource
import com.prac.data.fake.source.network.FakeAuthApiDataSource
import com.prac.data.fake.source.network.FakeUserApiDataSource
import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.data.repository.model.TokenModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.ZonedDateTime

class TokenRepositoryTest {

    private lateinit var tokenLocalDataSource: FakeTokenLocalDataSource
    private lateinit var authApiDataSource: FakeAuthApiDataSource
    private lateinit var userApiDataSource: FakeUserApiDataSource
    private lateinit var userLocalDataSource: FakeUserLocalDataSource

    private lateinit var tokenRepository: TokenRepository

    private val code = "code"
    private val token = TokenModel("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())

    @Before
    fun setUp() {
        tokenLocalDataSource = FakeTokenLocalDataSource()
        authApiDataSource = FakeAuthApiDataSource(token)
        userApiDataSource = FakeUserApiDataSource()
        userLocalDataSource = FakeUserLocalDataSource()
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource,
            userApiDataSource = userApiDataSource,
            userLocalDataSource = userLocalDataSource
        )
    }

    @Test
    fun authorizeOAuth_updateCacheAndReturnSuccess() = runTest {
        val expectedUserName = "test"

        val result = tokenRepository.authorizeOAuth(code)

        val cache = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertEquals(cache.accessToken, token.accessToken)
        assertEquals(cache.refreshToken, token.refreshToken)
        assertEquals(cache.expiresInSeconds, token.expiresInSeconds)
        assertEquals(cache.refreshTokenExpiresInSeconds, token.refreshTokenExpiresInSeconds)
        assertEquals(cache.updatedAt, token.updatedAt)
        assertTrue(result.isSuccess)
        assertEquals(userName, expectedUserName)
    }

    @Test
    fun authorizeOAuth_returnNetworkError() = runTest {
        authApiDataSource.setThrowable(IOException())

        val result = tokenRepository.authorizeOAuth(code)

        val cache = tokenLocalDataSource.getToken()
        assertTrue(cache.accessToken.isEmpty())
        assertTrue(cache.refreshToken.isEmpty())
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun authorizeOAuth_returnAuthorizationError() = runTest {
        authApiDataSource.setThrowable(IllegalArgumentException())

        val result = tokenRepository.authorizeOAuth(code)

        val cache = tokenLocalDataSource.getToken()
        assertTrue(cache.accessToken.isEmpty())
        assertTrue(cache.refreshToken.isEmpty())
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun refreshToken_updateCacheAndReturnSuccess() = runTest {
        tokenRepository.authorizeOAuth(code)

        val result = tokenRepository.refreshToken(token.refreshToken)

        val cache = tokenLocalDataSource.getToken()
        assertNotEquals(cache.accessToken, token.accessToken)
        assertNotEquals(cache.accessToken, token.accessToken)
        assertTrue(result.isSuccess)
    }

    @Test
    fun refreshToken_returnFailure() = runTest {
        tokenRepository.authorizeOAuth(code)
        authApiDataSource.setThrowable(Exception())

        val result = tokenRepository.refreshToken(token.refreshToken)

        val cache = tokenLocalDataSource.getToken()
        assertEquals(cache.accessToken, token.accessToken)
        assertEquals(cache.accessToken, token.accessToken)
        assertTrue(result.isFailure)
    }

    @Test
    fun isLoggedIn_accessTokenExist_returnTrue() = runTest {
        tokenRepository.authorizeOAuth(code)

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertTrue(isLoggedIn)
    }

    @Test
    fun isLoggedIn_accessTokenDoesNotExist_returnFalse() = runTest {

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertFalse(isLoggedIn)
    }

    @Test
    fun getAccessToken_returnTokenFromDataSource() = runTest {
        tokenRepository.authorizeOAuth(code)

        val result = tokenRepository.getAccessToken()

        assertEquals(result, token.accessToken)
    }

    @Test
    fun getRefreshToken_returnTokenFromDataSource() = runTest {
        tokenRepository.authorizeOAuth(code)

        val result = tokenRepository.getRefreshToken()

        assertEquals(result, token.refreshToken)
    }

    @Test
    fun getAccessTokenIsExpired_returnFalseFromDataSource() = runTest {
        tokenRepository.authorizeOAuth(code)

        val result = tokenRepository.getAccessTokenIsExpired()

        assertFalse(result)
    }

    @Test
    fun getAccessTokenIsExpired_returnTrueFromDataSource() = runTest {
        val expiredToken = TokenModel("accessToken", "refreshToken", 1, 1, ZonedDateTime.now())
        authApiDataSource = FakeAuthApiDataSource(expiredToken)
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource,
            userApiDataSource = userApiDataSource,
            userLocalDataSource = userLocalDataSource
        )
        tokenRepository.authorizeOAuth(code)
        Thread.sleep(1500)

        val result = tokenRepository.getAccessTokenIsExpired()

        assertTrue(result)
    }

    @Test
    fun getRefreshTokenIsExpired_returnFalseFromDataSource() = runTest {
        tokenRepository.authorizeOAuth(code)

        val result = tokenRepository.getRefreshTokenIsExpired()

        assertFalse(result)
    }

    @Test
    fun getRefreshTokenIsExpired_returnTrueFromDataSource() = runTest {
        val expiredToken = TokenModel("accessToken", "refreshToken", 1, 1, ZonedDateTime.now())
        authApiDataSource = FakeAuthApiDataSource(expiredToken)
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource,
            userApiDataSource = userApiDataSource,
            userLocalDataSource = userLocalDataSource
        )
        tokenRepository.authorizeOAuth(code)
        Thread.sleep(1500)

        val result = tokenRepository.getRefreshTokenIsExpired()

        assertTrue(result)
    }
}