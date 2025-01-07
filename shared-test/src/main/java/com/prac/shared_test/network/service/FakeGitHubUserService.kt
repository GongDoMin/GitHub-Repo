package com.prac.shared_test.network.service

import com.prac.network.model.AccessTokenRequest
import com.prac.network.model.UserDto
import com.prac.network.service.GitHubUserService

class FakeGitHubUserService(
    private val user: UserDto
) : GitHubUserService {
    override suspend fun getUserInformation(clientId: String, accept: String, authorization: String, accessToken: AccessTokenRequest): UserDto {
        return user
    }
}