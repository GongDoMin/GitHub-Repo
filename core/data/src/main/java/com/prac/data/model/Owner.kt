package com.prac.data.model

import com.prac.local.model.OwnerEntity
import com.prac.network.model.response.OwnerResponse

data class Owner(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerResponse.toOwnerModel() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )

fun OwnerEntity.toOwnerModel() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )

fun Owner.toOwner() =
    OwnerEntity(
        login = login,
        avatarUrl = avatarUrl
    )