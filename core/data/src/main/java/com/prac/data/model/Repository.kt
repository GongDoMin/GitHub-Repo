package com.prac.data.model

import com.prac.local.model.RepositoryEntity
import com.prac.network.model.response.RepositoryResponse

data class Repository(
    val id: Int = 0,
    val name: String = "",
    val owner: Owner = Owner(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    val isStarred: Boolean? = null
)

fun RepositoryResponse.toRepoModel() =
    Repository(
        id = id,
        name = name,
        owner = owner.toOwnerModel(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = null
    )

fun RepositoryEntity.toRepoModel() =
    Repository(
        id = id,
        name = name,
        owner = owner.toOwnerModel(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )

fun Repository.toRepository() =
    RepositoryEntity(
        id = id,
        name = name,
        owner = owner.toOwner(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )
