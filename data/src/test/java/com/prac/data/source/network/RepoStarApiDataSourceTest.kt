package com.prac.data.source.network

import com.prac.data.source.network.impl.RepoStarApiDataSourceImpl
import com.prac.data.source.network.service.GitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
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

    @Test
    fun checkRepositoryIsStarred_callGitHubService() = runTest {
        val repoName = "testRepo"

        repoStarApiDataSource.checkRepositoryIsStarred(repoName)

        verify(gitHubService).checkRepositoryIsStarred("GongDoMin", repoName)
    }

    @Test
    fun starRepository_callGitHubService() = runTest {
        val userName = "testUser"
        val repoName = "testRepo"

        repoStarApiDataSource.starRepository(userName, repoName)

        verify(gitHubService).starRepository(userName, repoName)
    }
}