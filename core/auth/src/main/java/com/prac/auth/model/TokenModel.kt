package com.prac.auth.model

import com.prac.local.model.TokenEntity
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiredIn: Int = 0,
    val refreshExpiredIn: Int = 0,
    val updatedAt: ZonedDateTime = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
)

fun TokenModel.toTokenLocalDto() =
    TokenEntity(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresInSeconds = expiredIn,
        refreshTokenExpiresInSeconds = refreshExpiredIn,
        updatedAt = updatedAt
    )