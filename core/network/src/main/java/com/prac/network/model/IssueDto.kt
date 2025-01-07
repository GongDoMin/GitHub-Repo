package com.prac.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueDto(
    @SerialName("id") val id: Int = 0
)