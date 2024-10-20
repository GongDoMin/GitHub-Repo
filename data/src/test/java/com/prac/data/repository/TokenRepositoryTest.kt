package com.prac.data.repository

import com.prac.data.exception.CommonException
import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.data.repository.model.TokenModel
import com.prac.local.fake.source.FakeTokenLocalDataSource
import com.prac.local.fake.source.FakeUserLocalDataSource
import com.prac.network.fake.FakeAuthApiDataSource
import com.prac.network.fake.FakeUserApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Before
    fun setUp() {
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

    @Test
    fun authorizeOAuth_apiCallIsSuccess_successAndUpdateTokenAndUserName() = runTest {
        val expectedUserName = "test"
        val expectedToken = TokenModel(
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
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun authorizeOAuth_authorizeOAuthIsFailure_authorizationErrorAndNotUpdateTokenAndUserName() = runTest {
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
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun authorizeOAuth_getUserNameIsFailure_networkErrorAndNotUpdateTokenAndUserName() = runTest {
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
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun authorizeOAuth_getUserNameIsFailure_authorizationErrorAndNotUpdateTokenAndUserName() = runTest {
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
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun refreshToken_refreshTokenIsSuccess_successAndUpdateToken() = runTest {
        tokenLocalDataSource.setInitialToken()
        val refreshToken = "refreshToken"
        val expectedToken = TokenModel(
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
        tokenLocalDataSource.setInitialToken()
        val refreshToken = "refreshToken"
        val expectedToken = TokenModel(
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
        tokenLocalDataSource.setInitialToken()

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertTrue(isLoggedIn)
    }

    @Test
    fun isLoggedIn_tokenIsNotExist_false() = runTest {

        val isLoggedIn = tokenRepository.isLoggedIn()

        assertFalse(isLoggedIn)
    }
}