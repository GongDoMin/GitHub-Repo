package com.prac.data.model

import com.prac.local.room.entity.Repository
import com.prac.network.dto.RepoDto

data class RepoModel(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerModel = OwnerModel(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    var isStarred: Boolean? = null
)

fun RepoDto.toRepoModel() =
    RepoModel(
        id = id,
        name = name,
        owner = owner.toOwnerModel(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = null
    )

fun RepoModel.toRepository() =
    Repository(
        id = id,
        name = name,
        owner = owner.toOwner(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )
