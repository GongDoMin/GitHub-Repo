package com.prac.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueResponse(
    @SerialName("id") val id: Int = 0
)