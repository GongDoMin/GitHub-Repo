package com.prac.data.fake.source.local

import com.prac.data.source.local.datastore.TokenDataStoreManager
import com.prac.data.source.local.datastore.TokenLocalDto
import java.time.Instant
import java.time.ZoneId

internal class FakeTokenDataStoreManager(
    private var token: TokenLocalDto = TokenLocalDto(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )
) : TokenDataStoreManager {
    override suspend fun saveTokenData(token: TokenLocalDto) {
        this.token = token
    }

    override suspend fun getToken(): TokenLocalDto {
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