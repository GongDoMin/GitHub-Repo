package com.prac.data.source.network

import com.prac.data.source.network.impl.AuthApiDataSourceImpl
import com.prac.data.source.network.service.GitHubAuthService
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class AuthApiDataSourceTest {

    @Mock private lateinit var gitHubAuthService: GitHubAuthService
    private lateinit var authApiDataSource: AuthApiDataSource

    @Before
    fun setup() {
        authApiDataSource = AuthApiDataSourceImpl(gitHubAuthService)
    }
}