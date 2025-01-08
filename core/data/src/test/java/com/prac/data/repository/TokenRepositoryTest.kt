package com.prac.data.repository

import com.prac.data.impl.TokenRepositoryImpl
import com.prac.local.TokenLocalDataSource
import com.prac.local.model.TokenEntity
import com.prac.network.AuthApiDataSource
import com.prac.network.model.response.TokenResponse
import com.prac.shared_test.local.source.FakeTokenLocalDataSource
import com.prac.shared_test.network.FakeAuthApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZonedDateTime

class TokenRepositoryTest {

    private lateinit var tokenLocalDataSource: TokenLocalDataSource
    private lateinit var authApiDataSource: AuthApiDataSource

    private lateinit var tokenRepository: TokenRepository

    @Test
    fun OAuth_인증_성공시_토큰_업데이트() = runTest {
        // given
        initialTokenRepositoryWithNothing()

        // when
        tokenRepository.authorizeOAuth("test")

        // then
        val token = tokenLocalDataSource.getToken()
        assertEquals(token.accessToken, fakeRemoteToken.accessToken)
        assertEquals(token.refreshToken, fakeRemoteToken.refreshToken)
        assertEquals(token.expiresInSeconds, fakeRemoteToken.expiresIn)
        assertEquals(token.refreshTokenExpiresInSeconds, fakeRemoteToken.refreshTokenExpiresIn)
    }

    @Test
    fun 토큰존재시_true_반환() = runTest {
        // given
        initialTokenRepositoryWithFakeToken()

        // when
        val result = tokenRepository.isLoggedIn()

        // then
        assertTrue(result)
    }

    @Test
    fun 토큰이_존재하지않을떄_false_반환() = runTest {
        // given
        initialTokenRepositoryWithNothing()

        // when
        val result = tokenRepository.isLoggedIn()

        // then
        assertFalse(result)
    }

    @Test
    fun 토큰_초기화후_빈토큰_반환() = runTest {
        // then
        initialTokenRepositoryWithFakeToken()

        // when
        tokenRepository.clearToken()

        // then
        val token = tokenLocalDataSource.getToken()
        assertTrue(token.accessToken.isEmpty())
        assertTrue(token.refreshToken.isEmpty())
        assertEquals(token.expiresInSeconds, 0)
        assertEquals(token.refreshTokenExpiresInSeconds, 0)
    }

    private fun initialTokenRepositoryWithNothing() {
        tokenLocalDataSource = FakeTokenLocalDataSource()
        authApiDataSource = FakeAuthApiDataSource(fakeRemoteToken)
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource
        )
    }

    private fun initialTokenRepositoryWithFakeToken() {
        tokenLocalDataSource = FakeTokenLocalDataSource(fakeLocalToken)
        authApiDataSource = FakeAuthApiDataSource(fakeRemoteToken)
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource
        )
    }

    companion object {
        private val fakeRemoteToken = TokenResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresIn = 3600,
            refreshTokenExpiresIn = 3600,
            scope = "",
            tokenType = "Bearer"
        )

        private val fakeLocalToken = TokenEntity(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now()
        )
    }
}