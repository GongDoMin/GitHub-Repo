package com.prac.data.source.local

import com.prac.data.source.local.datastore.TokenDataStoreManager
import com.prac.data.source.local.datastore.TokenLocalDto
import com.prac.data.source.local.impl.TokenLocalDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.ZoneId

@RunWith(MockitoJUnitRunner::class)
internal class TokenLocalDataSourceTest {

    @Mock private lateinit var tokenDataStoreManager: TokenDataStoreManager
    private lateinit var tokenLocalDataSource: TokenLocalDataSource

    @After
    fun tearDown() = runTest {
        tokenLocalDataSource.clearToken()
    }

    @Test
    fun getCachedToken_returnEmptyTokenWhenDataStoreIsEmpty() = runTest {
        whenever(tokenDataStoreManager.getToken())
            .thenReturn(
                TokenLocalDto(
                    "",
                    "",
                    0,
                    0,
                    Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
                )
            )
        tokenLocalDataSource = TokenLocalDataSourceImpl(tokenDataStoreManager)

        val result = tokenLocalDataSource.getToken()

        assertTrue(result.accessToken.isEmpty())
        assertTrue(result.refreshToken.isEmpty())
    }
}