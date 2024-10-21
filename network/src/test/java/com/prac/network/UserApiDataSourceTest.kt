package com.prac.network

import com.prac.network.dto.OwnerDto
import com.prac.network.dto.UserDto
import com.prac.network.fake.service.FakeGitHubUserService
import com.prac.network.impl.UserApiDataSourceImpl
import com.prac.network.service.GitHubUserService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserApiDataSourceTest {

    private lateinit var gitHubUserService: FakeGitHubUserService
    private lateinit var repoStarApiDataSource: UserApiDataSource

    @Before
    fun setUp() {
        gitHubUserService = FakeGitHubUserService()
        repoStarApiDataSource = UserApiDataSourceImpl(gitHubUserService)
    }

    @Test
    fun getUser_whenCalled_user() = runTest {
        val accessToken = "test"
        val expectedUser = UserDto(
            user = OwnerDto(
                login = "test",
                avatarUrl = "test"
            )
        )

        val result = repoStarApiDataSource.getUserName(accessToken)

        assertEquals(result, expectedUser.user.login)
    }
}
