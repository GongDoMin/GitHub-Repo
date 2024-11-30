package com.prac.data.model

data class RepoModel(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerModel = OwnerModel(),
    val stargazersCount: Int = 0,
    val defaultBranch: String = "",
    val updatedAt: String = "",
    var isStarred: Boolean? = null
)