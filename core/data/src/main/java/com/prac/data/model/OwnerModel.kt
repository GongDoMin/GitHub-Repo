package com.prac.data.model

import com.prac.local.room.entity.Owner
import com.prac.network.dto.OwnerDto

data class OwnerModel(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerDto.toOwnerModel() =
    OwnerModel(
        login = login,
        avatarUrl = avatarUrl
    )

fun OwnerModel.toOwner() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )