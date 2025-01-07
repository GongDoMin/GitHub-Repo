package com.prac.data.repository

import com.prac.data.impl.TokenRepositoryImpl
import com.prac.local.model.TokenEntity
import com.prac.shared_test.local.source.FakeTokenLocalDataSource
import com.prac.shared_test.network.FakeAuthApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class TokenEntityRepositoryEntityTest {

    private lateinit var tokenLocalDataSource: FakeTokenLocalDataSource
    private lateinit var authApiDataSource: FakeAuthApiDataSource

    private lateinit var tokenRepository: TokenRepository

    private val token = TokenEntity(
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
        val expectedToken = TokenEntity(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now() // updatedAt 의 정확한 시간은 테스트할 수 없으므로 현재 시간을 사용
        )

        val result = tokenRepository.authorizeOAuth(code)

        val token = tokenLocalDataSource.getToken()
        assertEquals(token.accessToken, expectedToken.accessToken)
        assertEquals(token.refreshToken, expectedToken.refreshToken)
        assertEquals(token.expiresInSeconds, expectedToken.expiresInSeconds)
        assertEquals(token.refreshTokenExpiresInSeconds, expectedToken.refreshTokenExpiresInSeconds)
        assertEquals(result, expectedToken.accessToken)
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

        tokenRepository.clearToken()

        val token = tokenLocalDataSource.getToken()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
    }

    private fun makeTokenRepository() {
        tokenLocalDataSource = FakeTokenLocalDataSource()
        authApiDataSource = FakeAuthApiDataSource()
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource
        )
    }

    private fun makeTokenRepositoryWithInitialToken() {
        tokenLocalDataSource = FakeTokenLocalDataSource(token)
        authApiDataSource = FakeAuthApiDataSource()
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource
        )
    }
}