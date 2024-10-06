package com.prac.data.source.local.datastore

import java.time.ZonedDateTime

internal data class TokenLocalDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresInMinute: Int,
    val refreshTokenExpiresInMinute: Int,
    val updatedAt: ZonedDateTime
) {
    val isExpired: Boolean
        get() = updatedAt.plusMinutes(expiresInMinute.toLong()).isBefore(ZonedDateTime.now())

    val isRefreshTokenExpired: Boolean
        get() = updatedAt.plusMinutes(refreshTokenExpiresInMinute.toLong()).isBefore(ZonedDateTime.now())
}