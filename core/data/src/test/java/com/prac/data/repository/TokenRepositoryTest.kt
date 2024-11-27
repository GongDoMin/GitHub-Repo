package com.prac.data.repository

import com.prac.exception.CommonException
import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.shared_test.local.source.FakeTokenLocalDataSource
import com.prac.shared_test.local.source.FakeUserLocalDataSource
import com.prac.shared_test.network.FakeAuthApiDataSource
import com.prac.shared_test.network.FakeUserApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class TokenRepositoryTest {

    private lateinit var tokenLocalDataSource: FakeTokenLocalDataSource
    private lateinit var authApiDataSource: FakeAuthApiDataSource
    private lateinit var userApiDataSource: FakeUserApiDataSource
    private lateinit var userLocalDataSource: FakeUserLocalDataSource

    private lateinit var tokenRepository: TokenRepository

    private val token = TokenLocalDto(
        accessToken = "accessToken",
        refreshToken = "refreshToken",
        expiresInSeconds = 3600,
        refreshTokenExpiresInSeconds = 3600,
        updatedAt = Instant.now().atZone(ZoneId.systemDefault())
    )
    private val code = "code"

    @Test
    fun authorizeOAuth_apiCallIsSuccess_successAndUpdateTokenAndUserName() = runTest {
        makeTokenRepository()
        val expectedUserName = "test"
        val expectedToken = TokenLocalDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now() // updatedAt 의 정확한 시간은 테스트할 수 없으므로 현재 시간을 사용
        )

        val result = tokenRepository.authorizeOAuth(code)

        val token = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertEquals(token.accessToken, expectedToken.accessToken)
        assertEquals(token.refreshToken, expectedToken.refreshToken)
        assertEquals(token.expiresInSeconds, expectedToken.expiresInSeconds)
        assertEquals(token.refreshTokenExpiresInSeconds, expectedToken.refreshTokenExpiresInSeconds)
        assertEquals(userName, expectedUserName)
        assertTrue(result.isSuccess)
    }

    @Test
    fun authorizeOAuth_authorizeOAuthIsFailure_networkErrorAndNotUpdateTokenAndUserName() = runTest {
        makeTokenRepository()
        authApiDataSource.setThrowable(IOException())

        val result = tokenRepository.authorizeOAuth(code)

        val token = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
        assertEquals(token.expiresInSeconds, 0)
        assertEquals(token.refreshTokenExpiresInSeconds, 0)
        assertTrue(userName.isEmpty())
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is com.prac.exception.CommonException.NetworkError)
    }

    @Test
    fun authorizeOAuth_authorizeOAuthIsFailure_authorizationErrorAndNotUpdateTokenAndUserName() = runTest {
        makeTokenRepository()
        val exception = IllegalArgumentException() // IOException 제외한 모든 에러는 authorizationError 로 처리하기 때문에 Exception 의 종류는 상관없음.
        authApiDataSource.setThrowable(exception)

        val result = tokenRepository.authorizeOAuth(code)

        val token = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
        assertEquals(token.expiresInSeconds, 0)
        assertEquals(token.refreshTokenExpiresInSeconds, 0)
        assertTrue(userName.isEmpty())
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is com.prac.exception.CommonException.AuthorizationError)
    }

    @Test
    fun authorizeOAuth_getUserNameIsFailure_networkErrorAndNotUpdateTokenAndUserName() = runTest {
        makeTokenRepository()
        userApiDataSource.setThrowable(IOException())

        val result = tokenRepository.authorizeOAuth(code)

        val token = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
        assertEquals(token.expiresInSeconds, 0)
        assertEquals(token.refreshTokenExpiresInSeconds, 0)
        assertTrue(userName.isEmpty())
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is com.prac.exception.CommonException.NetworkError)
    }

    @Test
    fun authorizeOAuth_getUserNameIsFailure_authorizationErrorAndNotUpdateTokenAndUserName() = runTest {
        makeTokenRepository()
        val exception = IllegalArgumentException() // IOException 제외한 모든 에러는 authorizationError 로 처리하기 때문에 Exception 의 종류는 상관없음.
        authApiDataSource.setThrowable(exception)

        val result = tokenRepository.authorizeOAuth(code)

        val token = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
        assertEquals(token.expiresInSeconds, 0)
        assertEquals(token.refreshTokenExpiresInSeconds, 0)
        assertTrue(userName.isEmpty())
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is com.prac.exception.CommonException.AuthorizationError)
    }

    @Test
    fun refreshToken_refreshTokenIsSuccess_successAndUpdateToken() = runTest {
        makeTokenRepositoryWithInitialToken()
        val refreshToken = "refreshToken"
        val expectedToken = TokenLocalDto(
            accessToken = "refreshAccessToken",
            refreshToken = "refreshRefreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now() // updatedAt 의 정확한 시간은 테스트할 수 없으므로 현재 시간을 사용
        )

        val result = tokenRepository.refreshToken(refreshToken)

        val token = tokenLocalDataSource.getToken()
        assertEquals(token.accessToken, expectedToken.accessToken)
        assertEquals(token.refreshToken, expectedToken.refreshToken)
        assertEquals(token.expiresInSeconds, expectedToken.expiresInSeconds)
        assertEquals(token.refreshTokenExpiresInSeconds, expectedToken.refreshTokenExpiresInSeconds)
        assertTrue(result.isSuccess)
    }

    @Test
    fun refreshToken_refreshTokenIsFailure_errorAndNotUpdateToken() = runTest {
        makeTokenRepositoryWithInitialToken()
        val refreshToken = "refreshToken"
        val expectedToken = TokenLocalDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now() // updatedAt 의 정확한 시간은 테스트할 수 없으므로 현재 시간을 사용
        )
        authApiDataSource.setThrowable(Exception())

        val result = tokenRepository.refreshToken(refreshToken)

        val token = tokenLocalDataSource.getToken()
        assertEquals(token.accessToken, expectedToken.accessToken)
        assertEquals(token.refreshToken, expectedToken.refreshToken)
        assertEquals(token.expiresInSeconds, expectedToken.expiresInSeconds)
        assertEquals(token.refreshTokenExpiresInSeconds, expectedToken.refreshTokenExpiresInSeconds)
        assertTrue(result.isFailure)
    }

    @Test
    fun isLoggedIn_tokenIsExist_true() = runTest {
        makeTokenRepositoryWithInitialToken()

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertTrue(isLoggedIn)
    }

    @Test
    fun isLoggedIn_tokenIsNotExist_false() = runTest {
        makeTokenRepository()

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertFalse(isLoggedIn)
    }

    @Test
    fun clearToken_clearTokenAndUserName_tokenAndUserNameIsEmpty() = runTest {
        makeTokenRepositoryWithInitialToken()
        userLocalDataSource.setUserName("test")

        tokenRepository.clearToken()

        val token = tokenLocalDataSource.getToken()
        val userName = userLocalDataSource.getUserName()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
        assertTrue(userName.isEmpty())
    }

    private fun makeTokenRepository() {
        tokenLocalDataSource = FakeTokenLocalDataSource()
        authApiDataSource = FakeAuthApiDataSource()
        userApiDataSource = FakeUserApiDataSource()
        userLocalDataSource = FakeUserLocalDataSource()
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource,
            userApiDataSource = userApiDataSource,
            userLocalDataSource = userLocalDataSource
        )
    }

    private fun makeTokenRepositoryWithInitialToken() {
        tokenLocalDataSource = FakeTokenLocalDataSource(token)
        authApiDataSource = FakeAuthApiDataSource()
        userApiDataSource = FakeUserApiDataSource()
        userLocalDataSource = FakeUserLocalDataSource()
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource,
            userApiDataSource = userApiDataSource,
            userLocalDataSource = userLocalDataSource
        )
    }
}