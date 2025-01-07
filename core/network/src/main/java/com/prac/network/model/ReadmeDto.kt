package com.prac.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadmeDto(
    @SerialName("content") val content: String = ""
)