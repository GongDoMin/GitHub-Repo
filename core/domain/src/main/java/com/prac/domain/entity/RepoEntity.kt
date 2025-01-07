package com.prac.domain.entity

import com.prac.data.model.Repository

data class RepoEntity(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerEntity = OwnerEntity(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    val isStarred: Boolean? = null
)

fun Repository.toRepoEntity() =
    RepoEntity(
        id = id,
        name = name,
        owner = owner.toOwnerEntity(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )