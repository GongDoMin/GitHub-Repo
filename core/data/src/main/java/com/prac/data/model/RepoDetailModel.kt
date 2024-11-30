package com.prac.data.model

data class RepoDetailModel(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerModel = OwnerModel(),
    val stargazersCount: Int = 0,
    val forksCount: Int = 0,
    var isStarred: Boolean? = null,
    val issueCount: Int = 0,
    val pullCount: Int = 0,
    val subscribeCount: Int = 0
)