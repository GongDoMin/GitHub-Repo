package com.prac.auth.model

import com.prac.local.model.TokenEntity
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresInSeconds: Int = 0,
    val refreshTokenExpiresInSeconds: Int = 0,
    val updatedAt: ZonedDateTime = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
)

internal fun TokenModel.toLocalModel() =
    TokenEntity(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresInSeconds = expiresInSeconds,
        refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds,
        updatedAt = updatedAt
    )