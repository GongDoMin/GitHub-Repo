package com.prac.data.fake.source.network.service

import com.prac.data.source.network.dto.AccessTokenRequest
import com.prac.data.source.network.dto.OwnerDto
import com.prac.data.source.network.dto.UserDto
import com.prac.data.source.network.service.GitHubUserService

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