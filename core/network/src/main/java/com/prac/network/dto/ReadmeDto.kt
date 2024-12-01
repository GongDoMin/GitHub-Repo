package com.prac.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadmeDto(
    @SerialName("content") val content: String = ""
)