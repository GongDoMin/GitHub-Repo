package com.prac.local.datastore.token

import java.time.ZonedDateTime

data class TokenLocalDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Int,
    val refreshTokenExpiresInSeconds: Int,
    val updatedAt: ZonedDateTime
) {
    val isExpired: Boolean
        get() = updatedAt.plusSeconds(expiresInSeconds.toLong()).isBefore(ZonedDateTime.now())

    val isRefreshTokenExpired: Boolean
        get() = updatedAt.plusSeconds(refreshTokenExpiresInSeconds.toLong()).isBefore(ZonedDateTime.now())
}