package com.prac.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullDto(
    @SerialName("id") val id: Int = 0
)