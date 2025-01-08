package com.prac.network

import com.prac.network.impl.RepoApiDataSourceImpl
import com.prac.network.model.response.OwnerResponse
import com.prac.network.model.response.RepositoryDetailResponse
import com.prac.network.model.response.RepositoryResponse
import com.prac.network.service.GitHubService
import com.prac.shared_test.network.service.FakeGitHubService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoApiDataSourceTest {

    private val gitHubService: GitHubService = FakeGitHubService()
    private val repoApiDatasource: RepoApiDataSource = RepoApiDataSourceImpl(gitHubService)

    @Test
    fun 레파지토리목록_요청시_레파지토리목록_반환() = runTest {
        // given
        val expectedRepositories = listOf(
            RepositoryResponse(0, "Repository 0", OwnerResponse("login 0", "avatar 0"), 5, "develop", "2023.01.05"),
            RepositoryResponse(1, "Repository 1", OwnerResponse("login 1", "avatar 1"), 5, "develop", "2023.01.05"),
            RepositoryResponse(2, "Repository 2", OwnerResponse("login 2", "avatar 2"), 5, "develop", "2023.01.05"),
            RepositoryResponse(3, "Repository 3", OwnerResponse("login 3", "avatar 3"), 5, "develop", "2023.01.05"),
            RepositoryResponse(4, "Repository 4", OwnerResponse("login 4", "avatar 4"), 5, "develop", "2023.01.05"),
            RepositoryResponse(5, "Repository 5", OwnerResponse("login 5", "avatar 5"), 5, "develop", "2023.01.05")
        )

        // when
        val result = repoApiDatasource.getRepositories("test", 1, 10)

        // then
        assertEquals(result.size, expectedRepositories.size)
        result.indices.forEach {
            assertEquals(result[it], expectedRepositories[it])
        }
    }

    @Test
    fun 레파지토리상세_요청시_레파지토리상세_반환() = runTest {
        // given
        val expectedRepositoryDetail =
            RepositoryDetailResponse(0, "Repository 0", OwnerResponse("login 0", "avatar 0"), 5, 5, 5)

        // when
        val result = repoApiDatasource.getRepository("login 0", "Repository 0")

        // then
        assertEquals(result, expectedRepositoryDetail)
    }

    @Test
    fun 이슈카운트_요청시_정수_반환() = runTest {
        // given
        val expectedIssueCount = 2

        // when
        val result = repoApiDatasource.getRepoIssueCount("login 0", "Repository 0")

        // then
        assertEquals(result, expectedIssueCount)
    }

    @Test
    fun 풀카운트_요청시_정수_반환() = runTest {
        // given
        val expectedIssueCount = 2

        // when
        val result = repoApiDatasource.getRepoPullCount("login 0", "Repository 0")

        // then
        assertEquals(result, expectedIssueCount)
    }

    @Test
    fun 리드미컨텐트_요청시_Base64를_통해_디코딩하고_문자열_반환() = runTest {
        // given
        val expectedContent = "hello world!!"

        // when
        val result = repoApiDatasource.getRepoReadme("login 0", "Repository 0")

        // then
        assertEquals(result, expectedContent)
    }
}