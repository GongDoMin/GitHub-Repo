package com.prac.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("user") val user: OwnerResponse = OwnerResponse()
)