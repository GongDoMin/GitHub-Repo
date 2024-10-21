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

    private lateinit var gitHubService: FakeGitHubService
    private lateinit var repoApiDatasource: RepoApiDataSource

    private val expectedRepoList = listOf(
        RepoDto(0, "test1", OwnerDto("test1", "test1"), 0, "master", "test1"),
        RepoDto(1, "test2", OwnerDto("test2", "test2"), 0, "master", "test1"),
        RepoDto(2, "test3", OwnerDto("test3", "test3"), 0, "master", "test1"),
        RepoDto(3, "test4", OwnerDto("test4", "test4"), 0, "master", "test1"),
    )

    @Before
    fun setUp() {
        gitHubService = FakeGitHubService()
        repoApiDatasource = RepoApiDataSourceImpl(gitHubService)
    }

    @Test
    fun getRepositories_whenCalled_repoDtoList() = runTest {
        val userName = "test"
        val perPage = 10
        val page = 1

        val result = repoApiDatasource.getRepositories(userName, perPage, page)

        assertEquals(result.size, expectedRepoList.size)
        result.indices.forEach {
            assertEquals(result[it].id, expectedRepoList[it].id)
            assertEquals(result[it].name, expectedRepoList[it].name)
            assertEquals(result[it].owner.login, expectedRepoList[it].owner.login)
            assertEquals(result[it].owner.avatarUrl, expectedRepoList[it].owner.avatarUrl)
            assertEquals(result[it].stargazersCount, expectedRepoList[it].stargazersCount)
            assertEquals(result[it].updatedAt, expectedRepoList[it].updatedAt)
        }
    }

    @Test
    fun getRepository_whenCalled_repoDetailDto() = runTest {
        val expectedRepo = expectedRepoList[0]

        val result = repoApiDatasource.getRepository(expectedRepo.owner.login, expectedRepo.name)

        assertEquals(result.id, expectedRepo.id)
        assertEquals(result.name, expectedRepo.name)
        assertEquals(result.owner.login, expectedRepo.owner.login)
        assertEquals(result.owner.avatarUrl, expectedRepo.owner.avatarUrl)
        assertEquals(result.stargazersCount, expectedRepo.stargazersCount)
    }
}