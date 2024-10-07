package com.prac.data.repository.model

import java.time.ZonedDateTime

internal data class TokenModel(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Int,
    val refreshTokenExpiresInSeconds: Int,
    val updatedAt: ZonedDateTime
)
