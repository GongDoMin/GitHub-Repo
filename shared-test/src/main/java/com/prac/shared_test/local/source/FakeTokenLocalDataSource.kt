package com.prac.shared_test.local.source

import com.prac.local.TokenLocalDataSource
import com.prac.local.model.TokenEntity
import java.time.Instant
import java.time.ZoneId

class FakeTokenLocalDataSource(
    private var token: TokenEntity = TokenEntity(
        accessToken = "",
        refreshToken = "",
        expiresInSeconds = 0,
        refreshTokenExpiresInSeconds = 0,
        updatedAt = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
    )
) : TokenLocalDataSource {

    override suspend fun setToken(token: TokenEntity) {
        this.token = token
    }

    override fun getToken(): TokenEntity {
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