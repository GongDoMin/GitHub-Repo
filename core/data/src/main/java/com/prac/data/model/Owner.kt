package com.prac.data.model

import com.prac.local.model.OwnerEntity
import com.prac.network.model.response.OwnerResponse

data class Owner(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerResponse.toModel() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )

fun OwnerEntity.toModel() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )

fun Owner.toLocalModel() =
    OwnerEntity(
        login = login,
        avatarUrl = avatarUrl
    )