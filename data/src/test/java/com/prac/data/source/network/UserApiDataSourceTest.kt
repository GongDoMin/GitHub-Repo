package com.prac.data.source.network

import com.prac.data.fake.source.network.service.FakeGitHubUserService
import com.prac.data.source.network.impl.UserApiDataSourceImpl
import com.prac.data.source.network.service.GitHubUserService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserApiDataSourceTest {

    private lateinit var gitHubUserService: GitHubUserService
    private lateinit var repoStarApiDataSource: UserApiDataSource

    @Before
    fun setUp() {
        gitHubUserService = FakeGitHubUserService()
        repoStarApiDataSource = UserApiDataSourceImpl(gitHubUserService)
    }

    @Test
    fun getUser_userNamePassedToDataSource() = runTest {
        val accessToken = "test"
        val expectedUserName = "test"

        val result = repoStarApiDataSource.getUserName(accessToken)

        assertEquals(result, expectedUserName)
    }
}
