package com.prac.local.local

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.datastore.token.TokenLocalDto
import com.prac.local.fake.FakeTokenDataStoreManager
import com.prac.local.impl.TokenLocalDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal class TokenLocalDataSourceTest {

    private lateinit var tokenDataStoreManager: TokenDataStoreManager
    private lateinit var tokenLocalDataSource: TokenLocalDataSource

    @After
    fun tearDown() = runTest {
        tokenLocalDataSource.clearToken()
    }

    @Test
    fun getCachedToken_returnEmptyTokenWhenDataStoreIsEmpty() = runTest {
        tokenDataStoreManager = FakeTokenDataStoreManager()
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        val result = tokenLocalDataSource.getToken()

        assertTrue(result.accessToken.isEmpty())
        assertTrue(result.refreshToken.isEmpty())
    }

    @Test
    fun getCachedToken_returnTokenWhenDataStoreIsNotEmpty() = runTest {
        tokenDataStoreManager = FakeTokenDataStoreManager(
            TokenLocalDto(
                "accessToken",
                "refreshToken",
                3600,
                3600,
                Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli()).atZone(ZoneId.systemDefault())
            )
        )
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        val result = tokenLocalDataSource.getToken()

        assertTrue(result.accessToken.isNotEmpty())
        assertTrue(result.refreshToken.isNotEmpty())
    }

    @Test
    fun setToken_updateCacheAndReturnCorrectToken() = runTest {
        val token = TokenLocalDto(
            "accessToken",
            "refreshToken",
            3600,
            3600,
            ZonedDateTime.now()
        )
        tokenDataStoreManager = FakeTokenDataStoreManager()
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        tokenLocalDataSource.setToken(token)

        val cacheResult = tokenLocalDataSource.getToken()
        val dataStoreResult = tokenDataStoreManager.getToken()
        assertEquals(cacheResult, token)
        assertEquals(dataStoreResult, token)
    }

    @Test
    fun clearToken_clearCacheAndDataStore() = runTest {
        val token = TokenLocalDto(
            "accessToken",
            "refreshToken",
            3600,
            3600,
            ZonedDateTime.now()
        )
        tokenDataStoreManager = FakeTokenDataStoreManager(token)
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