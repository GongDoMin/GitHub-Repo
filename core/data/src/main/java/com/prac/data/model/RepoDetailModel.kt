package com.prac.data.model

import com.prac.network.model.response.RepositoryDetailResponse

data class RepoDetailModel(
    val id: Int = 0,
    val name: String = "",
    val owner: OwnerModel = OwnerModel(),
    val stargazersCount: Int = 0,
    val forksCount: Int = 0,
    val isStarred: Boolean? = null,
    val issueCount: Int = 0,
    val pullCount: Int = 0,
    val subscribeCount: Int = 0,
    val readme: String = ""
)

fun RepositoryDetailResponse.toRepoDetailModel(
    issueCount: Int,
    pullCount: Int,
    readme: String
) =
    RepoDetailModel(
        id = id,
        name = name,
        owner = owner.toOwnerModel(),
        stargazersCount = stargazersCount,
        forksCount = forksCount,
        isStarred = null,
        issueCount = issueCount,
        pullCount = pullCount,
        subscribeCount = subscribersCount,
        readme = readme
    )