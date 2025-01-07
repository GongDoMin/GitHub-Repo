package com.prac.shared_test.network

import com.prac.network.RepoApiDataSource
import com.prac.network.model.response.OwnerResponse
import com.prac.network.model.response.RepoDetailResponse
import com.prac.network.model.response.RepoResponse

class FakeRepoApiDataSource : RepoApiDataSource {

    private val repoResponseList: MutableList<RepoResponse> = mutableListOf()
    private var starCount: Int? = 0
    private lateinit var throwable: Throwable

    fun setRepoDtoList(repoResponseList: List<RepoResponse>) {
        this.repoResponseList.clear() // paging fake 이기 때문에 이전의 존재했던 리스트를 초기화

        this.repoResponseList.addAll(repoResponseList)
    }

    fun setStarCount(starCount: Int) {
        this.starCount = starCount
    }

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepoResponse> {
        if (::throwable.isInitialized) throw throwable

        return repoResponseList
    }

    override suspend fun getRepository(userName: String, repoName: String): RepoDetailResponse {
        if (::throwable.isInitialized) throw throwable

        val repoDto = repoResponseList.find { it.owner.login == userName && it.name == repoName } ?: throw Exception("repository is not found")

        return RepoDetailResponse(
            id = repoDto.id,
            name = repoDto.name,
            owner = OwnerResponse(
                login = repoDto.owner.login,
                avatarUrl = repoDto.owner.avatarUrl),
            stargazersCount = starCount ?: repoDto.stargazersCount,
            forksCount = 0,
            subscribersCount = 0
        )
    }

    override suspend fun getRepoIssueCount(userName: String, repoName: String): Int {
        if (::throwable.isInitialized) throw throwable

        return 5
    }

    override suspend fun getRepoPullCount(userName: String, repoName: String): Int {
        if (::throwable.isInitialized) throw throwable

        return 5
    }

    override suspend fun getRepoReadme(userName: String, repoName: String): String {
        if (::throwable.isInitialized) throw throwable

        return "hi!"
    }
}