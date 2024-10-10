package com.prac.data.source.network

import com.prac.data.source.network.impl.RepoStarApiDataSourceImpl
import com.prac.data.source.network.service.GitHubService
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RepoStarApiDataSourceTest {

    @Mock
    private lateinit var gitHubService: GitHubService
    private lateinit var repoStarApiDataSource: RepoStarApiDataSource

    @Before
    fun setUp() {
        repoStarApiDataSource = RepoStarApiDataSourceImpl(gitHubService)
    }
}