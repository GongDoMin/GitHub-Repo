package com.prac.feature.detail.model

import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoDetailModel

data class RepositoryDetail(
    val id: Int = 0,
    val name: String = "",
    val owner: Owner = Owner(),
    val stargazersCount: Int = 0,
    val forksCount: Int = 0,
    val isStarred: Boolean? = null,
    val issueCount: Int = 0,
    val pullCount: Int = 0,
    val subscribeCount: Int = 0,
    val readme: String = ""
)

fun RepoDetailModel.toRepositoryDetail() =
    RepositoryDetail(
        id = id,
        name = name,
        owner = owner.toOwner(),
        stargazersCount = stargazersCount,
        forksCount = forksCount,
        issueCount = issueCount,
        pullCount = pullCount,
        subscribeCount = subscribeCount,
        readme = readme
    )
