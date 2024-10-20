package com.prac.local.local

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.local.fake.datastore.FakeTokenDataStoreManager
import com.prac.local.impl.TokenLocalDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZonedDateTime

internal class TokenLocalDataSourceTest {

    private lateinit var tokenDataStoreManager: FakeTokenDataStoreManager
    private lateinit var tokenLocalDataSource: TokenLocalDataSource

    @After
    fun tearDown() = runTest {
        tokenLocalDataSource.clearToken()
    }

    @Test
    fun getToken_dataStoreIsEmpty_emptyToken() = runTest {
        tokenDataStoreManager = FakeTokenDataStoreManager()
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        val result = tokenLocalDataSource.getToken()

        assertTrue(result.accessToken.isEmpty())
        assertTrue(result.refreshToken.isEmpty())
    }

    @Test
    fun getToken_dataStoreIsNotEmpty_notEmptyToken() = runTest {
        tokenDataStoreManager = FakeTokenDataStoreManager().apply { setInitialToken() }
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        val result = tokenLocalDataSource.getToken()

        assertTrue(result.accessToken.isNotEmpty())
        assertTrue(result.refreshToken.isNotEmpty())
    }

    @Test
    fun setToken_updateNewToken_localAndCachedNotEmptyToken() = runTest {
        val token = TokenLocalDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = ZonedDateTime.now()
        )
        tokenDataStoreManager = FakeTokenDataStoreManager()
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        tokenLocalDataSource.setToken(token)

        val cacheResult = tokenLocalDataSource.getToken()
        val localResult = tokenDataStoreManager.getToken()
        assertEquals(cacheResult, token)
        assertEquals(localResult, token)
    }

    @Test
    fun clearToken_updateEmptyToken_localAndCachedEmptyToken() = runTest {
        tokenDataStoreManager = FakeTokenDataStoreManager().apply { setInitialToken() }
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        tokenLocalDataSource.clearToken()

        val cacheResult = tokenLocalDataSource.getToken()
        val dataStoreResult = tokenDataStoreManager.getToken()
        assertTrue(cacheResult.accessToken.isEmpty())
        assertTrue(cacheResult.refreshToken.isEmpty())
        assertTrue(dataStoreResult.accessToken.isEmpty())
        assertTrue(dataStoreResult.refreshToken.isEmpty())
    }
}