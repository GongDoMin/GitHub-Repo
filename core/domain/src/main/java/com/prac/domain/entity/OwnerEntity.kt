package com.prac.domain.entity

import com.prac.data.model.Owner

data class OwnerEntity(
    val login: String = "",
    val avatarUrl: String = ""
)

fun Owner.toOwnerEntity() =
    OwnerEntity(
        login = login,
        avatarUrl = avatarUrl
    )