package com.prac.domain.entity

import com.prac.data.model.OwnerModel

data class OwnerEntity(
    val login: String = "",
    val avatarUrl: String = ""
)

fun OwnerModel.toOwnerEntity() =
    OwnerEntity(
        login = login,
        avatarUrl = avatarUrl
    )