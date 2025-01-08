package com.prac.local.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class TokenEntity(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresInSeconds: Int = 0,
    val refreshTokenExpiresInSeconds: Int = 0,
    val updatedAt: ZonedDateTime = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault())
) {
    val isExpired: Boolean
        get() = updatedAt.plusSeconds(expiresInSeconds.toLong()).isBefore(ZonedDateTime.now())

    val isRefreshTokenExpired: Boolean
        get() = updatedAt.plusSeconds(refreshTokenExpiresInSeconds.toLong()).isBefore(ZonedDateTime.now())
}