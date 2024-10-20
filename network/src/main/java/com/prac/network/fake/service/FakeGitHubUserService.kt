package com.prac.network.fake.service

import com.prac.network.dto.AccessTokenRequest
import com.prac.network.dto.OwnerDto
import com.prac.network.dto.UserDto
import com.prac.network.service.GitHubUserService

internal class FakeGitHubUserService : GitHubUserService {
    override suspend fun getUserInformation(clientId: String, accept: String, authorization: String, accessToken: AccessTokenRequest): UserDto {
        return UserDto(
            user = OwnerDto(
                login = "test",
                avatarUrl = "test"
            )
        )
    }
}