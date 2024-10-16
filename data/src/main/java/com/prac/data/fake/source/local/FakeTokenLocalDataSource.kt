package com.prac.data.fake.source.local

import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.datastore.TokenLocalDto
import java.time.Instant
import java.time.ZoneId

internal class FakeTokenLocalDataSource : TokenLocalDataSource {
    private var token = TokenLocalDto(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )

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