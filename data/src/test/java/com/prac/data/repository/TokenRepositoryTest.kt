package com.prac.data.repository

import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.network.AuthApiDataSource
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

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
}