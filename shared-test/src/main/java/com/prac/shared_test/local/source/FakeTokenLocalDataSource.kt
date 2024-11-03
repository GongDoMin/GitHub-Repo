package com.prac.shared_test.local.source

import com.prac.local.TokenLocalDataSource
import com.prac.local.datastore.token.TokenLocalDto
import java.time.Instant
import java.time.ZoneId

class FakeTokenLocalDataSource(
    private var token: TokenLocalDto = TokenLocalDto(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )
) : TokenLocalDataSource {

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