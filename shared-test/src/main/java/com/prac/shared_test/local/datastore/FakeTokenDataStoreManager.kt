package com.prac.shared_test.local.datastore

import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.model.TokenEntity
import java.time.Instant
import java.time.ZoneId

class FakeTokenDataStoreManager(
    private var token: TokenEntity = TokenEntity(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )
) : TokenDataStoreManager {

    override suspend fun setToken(token: TokenEntity) {
        this.token = token
    }

    override suspend fun getToken(): TokenEntity {
        return token
    }

    override suspend fun clearToken() {
        this.token = TokenEntity(
            accessToken = "",
            refreshToken = "",
            expiresInSeconds = 0,
            refreshTokenExpiresInSeconds = 0,
            updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
        )
    }
}