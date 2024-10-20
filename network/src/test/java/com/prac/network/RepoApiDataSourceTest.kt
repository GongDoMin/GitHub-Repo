package com.prac.network

import com.prac.network.dto.OwnerDto
import com.prac.network.dto.RepoDto
import com.prac.network.fake.service.FakeGitHubService
import com.prac.network.impl.RepoApiDataSourceImpl
import com.prac.network.service.GitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepoApiDataSourceTest {

    private lateinit var gitHubService: GitHubService
    private lateinit var repoApiDatasource: RepoApiDataSource

    private val repoList = listOf(
        RepoDto(0, "test1", OwnerDto("test1", "test1"), 0, "test1"),
        RepoDto(1, "test2", OwnerDto("test2", "test2"), 0, "test1"),
        RepoDto(2, "test3", OwnerDto("test3", "test3"), 0, "test1"),
        RepoDto(3, "test4", OwnerDto("test4", "test4"), 0, "test1"),
    )

    @Before
    fun setUp() {
        gitHubService = FakeGitHubService(repoList)
        repoApiDatasource = RepoApiDataSourceImpl(gitHubService)
    }

    @Test
    fun getRepositories_repositoriesPassedToDataSource() = runTest {
        val repoName = "test"
        val perPage = 10
        val page = 1

        val result = repoApiDatasource.getRepositories(repoName, perPage, page)

        result.indices.forEach {
            assertEquals(result[it].id, repoList[it].id)
            assertEquals(result[it].name, repoList[it].name)
            assertEquals(result[it].owner.login, repoList[it].owner.login)
            assertEquals(result[it].owner.avatarUrl, repoList[it].owner.avatarUrl)
            assertEquals(result[it].stargazersCount, repoList[it].stargazersCount)
            assertEquals(result[it].updatedAt, repoList[it].updatedAt)
        }
    }

    @Test
    fun getRepository_repositoryPassedToDataSource() = runTest {
        val repo = repoList[0]

        val result = repoApiDatasource.getRepository(repo.owner.login, repo.name)

        assertEquals(result.id, repo.id)
        assertEquals(result.name, repo.name)
        assertEquals(result.owner.login, repo.owner.login)
        assertEquals(result.owner.avatarUrl, repo.owner.avatarUrl)
        assertEquals(result.stargazersCount, repo.stargazersCount)
    }
}