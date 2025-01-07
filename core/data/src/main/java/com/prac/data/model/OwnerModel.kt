package com.prac.data.model

import com.prac.local.room.entity.Owner
import com.prac.network.model.response.OwnerResponse

data class OwnerModel(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerResponse.toOwnerModel() =
    OwnerModel(
        login = login,
        avatarUrl = avatarUrl
    )

fun Owner.toOwnerModel() =
    OwnerModel(
        login = login,
        avatarUrl = avatarUrl
    )

fun OwnerModel.toOwner() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )