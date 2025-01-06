package com.prac.feature.main.model

import com.prac.domain.entity.OwnerEntity

data class Owner(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerEntity.toOwner() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )