package com.prac.feature.detail.model

import com.prac.data.model.Owner

data class Owner(
    val login: String = "",
    val avatarUrl: String = ""
)

fun Owner.toOwner() =
    Owner(
        login = login,
        avatarUrl = avatarUrl
    )