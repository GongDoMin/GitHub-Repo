package com.prac.data.model

import com.prac.local.model.TokenEntity
import com.prac.network.model.response.TokenResponse
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal data class Token(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresInSeconds: Int = 0,
    val refreshTokenExpiresInSeconds: Int = 0,
    val updatedAt: ZonedDateTime = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
)

internal fun TokenResponse.toModel() =
    Token(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresInSeconds = expiresIn,
        refreshTokenExpiresInSeconds = refreshTokenExpiresIn,
        updatedAt = ZonedDateTime.now()
    )

internal fun Token.toLocalModel() =
    TokenEntity(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresInSeconds = expiresInSeconds,
        refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds,
        updatedAt = updatedAt
    )