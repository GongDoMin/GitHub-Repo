package com.prac.network

import com.prac.network.model.response.OwnerResponse
import com.prac.network.model.response.RepoResponse
import com.prac.network.impl.RepoApiDataSourceImpl
import com.prac.shared_test.network.service.FakeGitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class RepoApiDataSourceTest {

    private lateinit var gitHubService: FakeGitHubService
    private lateinit var repoApiDatasource: RepoApiDataSource

    private val repoList = listOf(
        RepoResponse(0, "test1", OwnerResponse("test1", "test1"), 0, "master", "test1"),
        RepoResponse(1, "test2", OwnerResponse("test2", "test2"), 0, "master", "test1"),
        RepoResponse(2, "test3", OwnerResponse("test3", "test3"), 0, "master", "test1"),
        RepoResponse(3, "test4", OwnerResponse("test4", "test4"), 0, "master", "test1"),
    )
    private val content = "hi!!"
    private val encoded = Base64.encode(content.toByteArray())

    @Before
    fun setUp() {
        gitHubService = FakeGitHubService(repoList, encoded)
        repoApiDatasource = RepoApiDataSourceImpl(gitHubService)
    }

    @Test
    fun getRepositories_whenCalled_repoDtoList() = runTest {
        val userName = "test"
        val perPage = 10
        val page = 1

        val result = repoApiDatasource.getRepositories(userName, perPage, page)

        assertEquals(result.size, repoList.size)
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
    fun getRepository_whenCalled_repoDetailDto() = runTest {
        val expectedRepo = repoList[0]

        val result = repoApiDatasource.getRepository(expectedRepo.owner.login, expectedRepo.name)

        assertEquals(result.id, expectedRepo.id)
        assertEquals(result.name, expectedRepo.name)
        assertEquals(result.owner.login, expectedRepo.owner.login)
        assertEquals(result.owner.avatarUrl, expectedRepo.owner.avatarUrl)
        assertEquals(result.stargazersCount, expectedRepo.stargazersCount)
    }

    @Test
    fun getIssueCount_whenCalled_returnInt() = runTest {
        val result = repoApiDatasource.getRepoIssueCount("", "")

        assertEquals(result, 2)
    }

    @Test
    fun getPullCount_whenCalled_returnInt() = runTest {
        val result = repoApiDatasource.getRepoPullCount("", "")

        assertEquals(result, 2)
    }

    @Test
    fun getRepoReadme_whenCalled_returnInt() = runTest {
        val result = repoApiDatasource.getRepoReadme("", "")

        assertEquals(result, content)
    }
}