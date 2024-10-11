package com.prac.data.source.local

import com.prac.data.source.local.datastore.TokenDataStoreManager
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class TokenLocalDataSourceTest {

    @Mock private lateinit var tokenDataStoreManager: TokenDataStoreManager
    private lateinit var tokenLocalDataSource: TokenLocalDataSource

    @After
    fun tearDown() = runTest {
        tokenLocalDataSource.clearToken()
    }
}