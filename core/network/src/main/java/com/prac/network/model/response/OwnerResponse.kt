package com.prac.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerResponse(
    @SerialName("login") val login: String = "",
    @SerialName("avatar_url") val avatarUrl: String = ""
)