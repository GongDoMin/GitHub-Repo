package com.prac.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserDto(
    @SerialName("user") val user: OwnerDto = OwnerDto()
)