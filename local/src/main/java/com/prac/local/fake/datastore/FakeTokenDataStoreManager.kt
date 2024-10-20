package com.prac.local.fake.datastore

import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.datastore.token.TokenLocalDto
import java.time.Instant
import java.time.ZoneId

class FakeTokenDataStoreManager : TokenDataStoreManager {

    private var token: TokenLocalDto = TokenLocalDto(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )

    fun setInitialToken() {
        token = token.copy(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresInSeconds = 3600,
            refreshTokenExpiresInSeconds = 3600,
            updatedAt = Instant.now().atZone(ZoneId.systemDefault())
        )
    }

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