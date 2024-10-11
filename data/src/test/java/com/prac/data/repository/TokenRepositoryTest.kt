package com.prac.data.repository

import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.data.repository.model.TokenModel
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.datastore.TokenLocalDto
import com.prac.data.source.network.AuthApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.ZonedDateTime

@RunWith(MockitoJUnitRunner::class)
class TokenRepositoryTest {

    @Mock private lateinit var tokenLocalDataSource: TokenLocalDataSource
    @Mock private lateinit var authApiDataSource: AuthApiDataSource

    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setUp() {
        tokenRepository = TokenRepositoryImpl(
            tokenLocalDataSource = tokenLocalDataSource,
            authApiDataSource = authApiDataSource
        )
    }

    @Test
    fun authorizeOAuth_updateDataStoreAndReturnSuccess() = runTest {
        val code = "testCode"
        val token = TokenModel("accessToken", "refreshToken", 3600, 3600, ZonedDateTime.now())
        whenever(authApiDataSource.authorizeOAuth(code)).thenReturn(token)

        val result = tokenRepository.authorizeOAuth(code)

        verify(tokenLocalDataSource)
            .setToken(
                TokenLocalDto(
                    token.accessToken,
                    token.refreshToken,
                    token.expiresInSeconds,
                    token.refreshTokenExpiresInSeconds,
                    token.updatedAt
                )
            )
        assertTrue(result.isSuccess)
    }
}