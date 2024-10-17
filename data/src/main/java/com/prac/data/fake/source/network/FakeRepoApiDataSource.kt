package com.prac.data.fake.source.network

import com.prac.data.repository.model.OwnerModel
import com.prac.data.repository.model.RepoDetailModel
import com.prac.data.repository.model.RepoModel
import com.prac.data.source.network.RepoApiDataSource
import com.prac.data.source.network.dto.RepoDto

internal class FakeRepoApiDataSource : RepoApiDataSource {

    private val repoDtoList: MutableList<RepoDto> = mutableListOf()
    private var starCount: Int? = 0
    private lateinit var throwable: Throwable

    fun setRepoDtoList(repoDtoList: List<RepoDto>) {
        this.repoDtoList.addAll(repoDtoList)
    }

    fun setStarCount(starCount: Int) {
        this.starCount = starCount
    }

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepoModel> {
        if (::throwable.isInitialized) {
            throw throwable
        }

        return repoDtoList.map { RepoModel(it.id, it.name, OwnerModel(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt) }
    }

    override suspend fun getRepository(userName: String, repoName: String): RepoDetailModel {
        if (::throwable.isInitialized) {
            throw throwable
        }

        val repoDto = repoDtoList.find { it.owner.login == userName && it.name == repoName } ?: RepoDto()

        return RepoDetailModel(
            repoDto.id,
            repoDto.name,
            OwnerModel(repoDto.owner.login, repoDto.owner.avatarUrl),
            starCount ?: repoDto.stargazersCount,
            0
        )
    }
}