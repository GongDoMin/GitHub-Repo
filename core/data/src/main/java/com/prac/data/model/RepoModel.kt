package com.prac.data.model

import com.prac.local.model.RepositoryEntity
import com.prac.network.model.response.RepositoryResponse

data class RepoModel(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerModel = OwnerModel(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    val isStarred: Boolean? = null
)

fun RepositoryResponse.toRepoModel() =
    RepoModel(
        id = id,
        name = name,
        owner = owner.toOwnerModel(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = null
    )

fun RepositoryEntity.toRepoModel() =
    RepoModel(
        id = id,
        name = name,
        owner = owner.toOwnerModel(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )

fun RepoModel.toRepository() =
    RepositoryEntity(
        id = id,
        name = name,
        owner = owner.toOwner(),
        stargazersCount = stargazersCount,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        isStarred = isStarred
    )
