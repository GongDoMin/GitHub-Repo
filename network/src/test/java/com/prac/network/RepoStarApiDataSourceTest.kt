package com.prac.network

import com.prac.network.impl.RepoStarApiDataSourceImpl
import com.prac.network.service.GitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RepoStarApiDataSourceTest {

    @Mock private lateinit var gitHubService: GitHubService
    private lateinit var repoStarApiDataSource: RepoStarApiDataSource

    @Before
    fun setUp() {
        repoStarApiDataSource = RepoStarApiDataSourceImpl(gitHubService)
    }

    @Test
    fun isStarred_whenCalled_callGitHubService() = runTest {
        val userName = "test"
        val repoName = "test"

        repoStarApiDataSource.isStarred(userName, repoName)

        verify(gitHubService).isStarred(userName, repoName)
    }

    @Test
    fun starRepository_whenCalled_callGitHubService() = runTest {
        val userName = "test"
        val repoName = "test"

        repoStarApiDataSource.starRepository(userName, repoName)

        verify(gitHubService).starRepository(userName, repoName)
    }

    @Test
    fun unStarRepository_whenCalled_callGitHubService() = runTest {
        val userName = "test"
        val repoName = "test"

        repoStarApiDataSource.unStarRepository(userName, repoName)

        verify(gitHubService).unStarRepository(userName, repoName)
    }
}