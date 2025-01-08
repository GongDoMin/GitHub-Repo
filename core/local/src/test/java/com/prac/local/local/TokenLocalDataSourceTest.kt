package com.prac.local.local

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.impl.TokenLocalDataSourceImpl
import com.prac.local.model.TokenEntity
import com.prac.shared_test.local.datastore.FakeTokenDataStoreManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal class TokenLocalDataSourceTest {

    private lateinit var tokenDataStoreManager: TokenDataStoreManager
    private lateinit var tokenLocalDataSource: TokenLocalDataSource

    @Test
    fun 데이터스토어의_토큰이_없을때_빈토큰_반환() = runTest {
        // given
        initialTokenLocalDataSourceWithNoting()

        // when
        val result = tokenLocalDataSource.getToken()

        // then
        assertTrue(result.accessToken.isEmpty())
        assertTrue(result.refreshToken.isEmpty())
        assertEquals(result.expiresInSeconds, 0)
        assertEquals(result.refreshTokenExpiresInSeconds, 0)
    }

    @Test
    fun 데이터스토어의_토큰이_있을때_토큰_반환() = runTest {
        // given
        initialTokenLocalDataSourceWithFakeToken()

        // when
        val result = tokenLocalDataSource.getToken()

        // then
        assertEquals(result, fakeToken)
    }

    @Test
    fun 새로운토큰_갱신_토큰_반환() = runTest {
        // given
        val expectedToken = TokenEntity(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now()
        )
        initialTokenLocalDataSourceWithNoting()

        // when
        tokenLocalDataSource.setToken(expectedToken)

        // then
        val result = tokenLocalDataSource.getToken()
        assertEquals(result, expectedToken)
    }

    @Test
    fun 토큰_초기화_빈토큰_반환() = runTest {
        // given
        initialTokenLocalDataSourceWithFakeToken()

        // when
        tokenLocalDataSource.clearToken()

        // then
        val result = tokenLocalDataSource.getToken()
        assertTrue(result.accessToken.isEmpty())
        assertTrue(result.refreshToken.isEmpty())
        assertEquals(result.expiresInSeconds, 0)
        assertEquals(result.refreshTokenExpiresInSeconds, 0)

    }

    private fun initialTokenLocalDataSourceWithNoting() {
        tokenDataStoreManager = FakeTokenDataStoreManager()
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)
    }

    private fun initialTokenLocalDataSourceWithFakeToken() {
        tokenDataStoreManager = FakeTokenDataStoreManager(fakeToken)
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)
    }

    companion object {
        private val fakeToken = TokenEntity(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now()
        )
    }
}