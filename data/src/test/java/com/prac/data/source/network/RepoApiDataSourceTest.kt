package com.prac.data.source.network

import com.prac.data.source.network.dto.OwnerDto
import com.prac.data.source.network.dto.RepoDto
import com.prac.data.source.network.impl.RepoApiDataSourceImpl
import com.prac.data.source.network.service.GitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class RepoApiDataSourceTest {

    @Mock private lateinit var gitHubService: GitHubService
    private lateinit var repoApiDatasource: RepoApiDataSource

    @Before
    fun setUp() {
        repoApiDatasource = RepoApiDataSourceImpl(gitHubService)
    }

    @Test
    fun getRepositories_repositoriesPassedToDataSource() = runTest {
        val repositoriesDto = listOf(
            RepoDto(0, "test1", OwnerDto("test1", "test1"), 0, "test1"),
            RepoDto(1, "test2", OwnerDto("test2", "test2"), 0, "test2"),
            RepoDto(2, "test3", OwnerDto("test3", "test3"), 0, "test3"),
        )
        val repoName = "test"
        val perPage = 10
        val page = 1
        whenever(gitHubService.getRepos(repoName, perPage, page)).thenReturn(repositoriesDto)

        val result = repoApiDatasource.getRepositories(repoName, perPage, page)

        result.indices.forEach {
            assertEquals(result[it].id, repositoriesDto[it].id)
            assertEquals(result[it].name, repositoriesDto[it].name)
            assertEquals(result[it].owner.login, repositoriesDto[it].owner.login)
            assertEquals(result[it].owner.avatarUrl, repositoriesDto[it].owner.avatarUrl)
            assertEquals(result[it].stargazersCount, repositoriesDto[it].stargazersCount)
            assertEquals(result[it].updatedAt, repositoriesDto[it].updatedAt)
        }
    }
}