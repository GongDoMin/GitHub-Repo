package com.prac.shared_test.network.service

import com.prac.network.dto.AccessTokenRequest
import com.prac.network.dto.OwnerDto
import com.prac.network.dto.UserDto
import com.prac.network.service.GitHubUserService

class FakeGitHubUserService(
    private val user: UserDto
) : GitHubUserService {
    override suspend fun getUserInformation(clientId: String, accept: String, authorization: String, accessToken: AccessTokenRequest): UserDto {
        return user
    }
}