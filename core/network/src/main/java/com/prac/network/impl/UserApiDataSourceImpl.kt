package com.prac.network.impl

import com.prac.network.UserApiDataSource
import com.prac.network.dto.AccessTokenRequest
import com.prac.network.service.GitHubUserService
import javax.inject.Inject

internal class UserApiDataSourceImpl @Inject constructor(
    private val githubUserService: GitHubUserService
) : UserApiDataSource {
    override suspend fun getUserName(accessToken: String): String {
        return githubUserService.getUserInformation(
            accessToken = AccessTokenRequest(accessToken)
        ).user.login
    }
}