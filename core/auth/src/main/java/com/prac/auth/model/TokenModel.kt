package com.prac.auth.model

import com.prac.local.datastore.token.TokenLocalDto
import java.time.ZonedDateTime

data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiredIn: Int = 0,
    val refreshExpiredIn: Int = 0,
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
)

fun TokenModel.toTokenLocalDto() =
    TokenLocalDto(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresInSeconds = expiredIn,
        refreshTokenExpiresInSeconds = refreshExpiredIn,
        updatedAt = updatedAt
    )