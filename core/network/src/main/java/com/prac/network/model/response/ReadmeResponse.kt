package com.prac.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadmeResponse(
    @SerialName("content") val content: String = ""
)