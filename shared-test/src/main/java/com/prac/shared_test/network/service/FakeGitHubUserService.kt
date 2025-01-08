package com.prac.shared_test.network.service

import com.prac.network.model.request.AccessTokenRequest
import com.prac.network.model.response.UserResponse
import com.prac.network.service.GitHubUserService
import kotlinx.serialization.json.Json
import java.io.File

class FakeGitHubUserService : GitHubUserService {

    private val json: Json = Json {
        ignoreUnknownKeys = true
    }
    private val user = File("src/main/assets/user.json").readText()

    override suspend fun getUserInformation(clientId: String, accept: String, authorization: String, accessToken: AccessTokenRequest): UserResponse =
        json.decodeFromString<UserResponse>(user)
}