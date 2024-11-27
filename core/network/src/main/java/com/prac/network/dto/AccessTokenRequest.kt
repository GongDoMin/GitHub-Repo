package com.prac.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequest(
    @SerialName("access_token") val accessToken: String
)