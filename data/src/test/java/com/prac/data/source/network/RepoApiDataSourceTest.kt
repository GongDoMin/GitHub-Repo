package com.prac.data.source.network

import com.prac.data.fake.source.network.service.FakeGitHubService
import com.prac.data.source.network.dto.OwnerDto
import com.prac.data.source.network.dto.RepoDto
import com.prac.data.source.network.impl.RepoApiDataSourceImpl
import com.prac.data.source.network.service.GitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepoApiDataSourceTest {

    private lateinit var gitHubService: GitHubService
    private lateinit var repoApiDatasource: RepoApiDataSource

    private val repos = listOf(
        RepoDto(0, "test1", OwnerDto("test1", "test1"), 0, "test1"),
        RepoDto(1, "test2", OwnerDto("test2", "test2"), 0, "test1"),
        RepoDto(2, "test3", OwnerDto("test3", "test3"), 0, "test1"),
        RepoDto(3, "test4", OwnerDto("test4", "test4"), 0, "test1"),
    )

    @Before
    fun setUp() {
        gitHubService = FakeGitHubService(repos)
        repoApiDatasource = RepoApiDataSourceImpl(gitHubService)
    }

    @Test
    fun getRepositories_repositoriesPassedToDataSource() = runTest {
        val repoName = "test"
        val perPage = 10
        val page = 1

        val result = repoApiDatasource.getRepositories(repoName, perPage, page)

        result.indices.forEach {
            assertEquals(result[it].id, repos[it].id)
            assertEquals(result[it].name, repos[it].name)
            assertEquals(result[it].owner.login, repos[it].owner.login)
            assertEquals(result[it].owner.avatarUrl, repos[it].owner.avatarUrl)
            assertEquals(result[it].stargazersCount, repos[it].stargazersCount)
            assertEquals(result[it].updatedAt, repos[it].updatedAt)
        }
    }

    @Test
    fun getRepository_repositoryPassedToDataSource() = runTest {
        val repo = repos[0]

        val result = repoApiDatasource.getRepository(repo.owner.login, repo.name)

        assertEquals(result.id, repo.id)
        assertEquals(result.name, repo.name)
        assertEquals(result.owner.login, repo.owner.login)
        assertEquals(result.owner.avatarUrl, repo.owner.avatarUrl)
        assertEquals(result.stargazersCount, repo.stargazersCount)
    }
}