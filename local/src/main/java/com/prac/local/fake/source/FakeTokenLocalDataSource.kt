package com.prac.local.fake.source

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import java.time.Instant
import java.time.ZoneId

class FakeTokenLocalDataSource : TokenLocalDataSource {

    private var token = TokenLocalDto(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )

    fun setInitialToken() {
        this.token = token.copy(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = Instant.now().atZone(ZoneId.systemDefault())
        )
    }

    override suspend fun setToken(token: TokenLocalDto) {
        this.token = token
    }

    override fun getToken(): TokenLocalDto {
        return token
    }

    override suspend fun clearToken() {
        this.token = TokenLocalDto(
            accessToken = "",
            refreshToken = "",
            expiresInSeconds = 0,
            refreshTokenExpiresInSeconds = 0,
            updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
        )
    }
}