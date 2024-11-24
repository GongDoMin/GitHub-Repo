package com.prac.data.entity

data class RepoEntity(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerEntity = OwnerEntity(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    var isStarred: Boolean? = null
)