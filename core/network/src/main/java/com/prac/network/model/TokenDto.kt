package com.prac.network.model

import com.prac.auth.model.TokenModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class TokenDto(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("expires_in") val expiresIn: Int = 0,
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("refresh_token_expires_in") val refreshTokenExpiresIn: Int = 0,
    @SerialName("scope") val scope: String = "",
    @SerialName("token_type") val tokenType: String = ""
)

fun TokenDto.toTokenModel() =
    TokenModel(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiredIn = expiresIn,
        refreshExpiredIn = refreshTokenExpiresIn,
        updatedAt = ZonedDateTime.now()
    )