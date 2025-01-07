package com.prac.network

import com.prac.network.model.OwnerDto
import com.prac.network.model.UserDto
import com.prac.shared_test.network.service.FakeGitHubUserService
import com.prac.network.impl.UserApiDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserApiDataSourceTest {

    private lateinit var gitHubUserService: FakeGitHubUserService
    private lateinit var repoStarApiDataSource: UserApiDataSource

    private val user = UserDto(
        user = OwnerDto(
            login = "test",
            avatarUrl = "test"
        )
    )

    @Before
    fun setUp() {
        gitHubUserService = FakeGitHubUserService(user)
        repoStarApiDataSource = UserApiDataSourceImpl(gitHubUserService)
    }

    @Test
    fun getUser_whenCalled_user() = runTest {
        val accessToken = "test"

        val result = repoStarApiDataSource.getUserName(accessToken)

        assertEquals(result, user.user.login)
    }
}