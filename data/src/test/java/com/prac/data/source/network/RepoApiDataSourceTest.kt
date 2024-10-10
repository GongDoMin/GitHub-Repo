package com.prac.data.source.network

import com.prac.data.source.network.impl.RepoApiDataSourceImpl
import com.prac.data.source.network.service.GitHubService
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RepoApiDataSourceTest {

    @Mock private lateinit var gitHubService: GitHubService
    private lateinit var repoApiDatasource: RepoApiDataSource

    @Before
    fun setUp() {
        repoApiDatasource = RepoApiDataSourceImpl(gitHubService)
    }
}