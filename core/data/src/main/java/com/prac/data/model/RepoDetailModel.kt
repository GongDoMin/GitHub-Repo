package com.prac.data.model

data class RepoDetailModel(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerModel = OwnerModel(),
    val stargazersCount: Int = 0,
    val forksCount: Int = 0,
    var isStarred: Boolean? = null,
    val issueSize: Int = 0,
    val pullSize: Int = 0,
    val subscribeSize: Int = 0
)