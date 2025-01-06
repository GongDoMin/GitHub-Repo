package com.prac.feature.main.model

import com.prac.domain.entity.RepoEntity

data class Repository(
    val id: Int = 0,
    val name: String = "",
    val owner: Owner = Owner(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    val isStarred: Boolean? = null
)

fun RepoEntity.toRepository() =
    Repository(
        id = id,
        name = name,
        owner = owner.toOwner(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )