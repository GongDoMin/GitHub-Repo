package com.prac.data.entity

data class RepoDetailEntity(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerEntity = OwnerEntity(),
    val stargazersCount: Int = 0,
    val forksCount: Int = 0,
    var isStarred: Boolean? = null
)