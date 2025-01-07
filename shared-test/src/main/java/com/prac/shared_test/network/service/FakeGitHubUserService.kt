package com.prac.shared_test.network.service

import com.prac.network.model.request.AccessTokenRequest
import com.prac.network.model.response.UserResponse
import com.prac.network.service.GitHubUserService

class FakeGitHubUserService(
    private val user: UserResponse
) : GitHubUserService {
    override suspend fun getUserInformation(clientId: String, accept: String, authorization: String, accessToken: AccessTokenRequest): UserResponse {
        return user
    }
}