package com.prac.auth.model

import java.time.ZonedDateTime

data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiredIn: Int = 0,
    val refreshExpiredIn: Int = 0,
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
)