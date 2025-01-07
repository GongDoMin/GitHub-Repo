package com.prac.data.model

import com.prac.local.model.OwnerEntity
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

fun OwnerEntity.toOwnerModel() =
    OwnerModel(
        login = login,
        avatarUrl = avatarUrl
    )

fun OwnerModel.toOwner() =
    OwnerEntity(
        login = login,
        avatarUrl = avatarUrl
    )