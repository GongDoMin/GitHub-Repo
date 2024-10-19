package com.prac.data.source.network.impl

import com.prac.data.source.network.UserApiDataSource
import com.prac.data.source.network.dto.AccessTokenRequest
import com.prac.data.source.network.service.GitHubUserService
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