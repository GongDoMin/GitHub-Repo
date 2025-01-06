package com.prac.feature.detail.model

import com.prac.data.model.OwnerModel

data class Owner(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerModel.toOwner() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )