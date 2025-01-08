package com.prac.shared_test.network.service

import com.prac.network.model.response.TokenResponse
import com.prac.network.service.GitHubAuthService
import kotlinx.serialization.json.Json
import java.io.File

class FakeGitHubAuthService: GitHubAuthService {

    private val json: Json = Json {
        ignoreUnknownKeys = true
    }
    private val token = File("src/main/assets/token.json").readText()
    private val refreshToken = File("src/main/assets/refreshToken.json").readText()

    override suspend fun authorizeOAuth(accept: String, clientID: String, clientSecret: String, code: String): TokenResponse =
        json.decodeFromString<TokenResponse>(token)

    override suspend fun refreshAccessToken(accept: String, clientID: String, clientSecret: String, grantType: String, refreshToken: String): TokenResponse =
        json.decodeFromString<TokenResponse>(this.refreshToken)
}
