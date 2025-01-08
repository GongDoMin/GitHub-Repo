package com.prac.network

import com.prac.network.impl.UserApiDataSourceImpl
import com.prac.network.model.response.OwnerResponse
import com.prac.network.service.GitHubUserService
import com.prac.shared_test.network.service.FakeGitHubUserService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UserApiDataSourceTest {

    private val gitHubUserService: GitHubUserService = FakeGitHubUserService()
    private val repoStarApiDataSource: UserApiDataSource = UserApiDataSourceImpl(gitHubUserService)

    @Test
    fun 사용자_요청시_사용자_반환() = runTest {
        // given
        val expectedUserName = "login 0"

        // when
        val result = repoStarApiDataSource.getUserName("accessToken")

        // then
        assertEquals(result, expectedUserName)
    }
}