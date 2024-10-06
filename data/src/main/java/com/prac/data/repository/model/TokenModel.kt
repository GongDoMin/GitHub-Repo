package com.prac.data.repository.model

import java.time.ZonedDateTime

internal data class TokenModel(
    val accessToken: String,
    val refreshToken: String,
    val expiresInMinute: Int,
    val refreshTokenExpiresInMinute: Int,
    val updatedAt: ZonedDateTime
)
