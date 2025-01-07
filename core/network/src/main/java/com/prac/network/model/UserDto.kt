package com.prac.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("user") val user: OwnerDto = OwnerDto()
)