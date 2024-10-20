package com.prac.network.fake

import com.prac.network.RepoApiDataSource
import com.prac.network.dto.OwnerDto
import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto

class FakeRepoApiDataSource : RepoApiDataSource {

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

    override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepoDto> {
        if (::throwable.isInitialized) {
            throw throwable
        }

        return repoDtoList
    }

    override suspend fun getRepository(userName: String, repoName: String): RepoDetailDto {
        if (::throwable.isInitialized) {
            throw throwable
        }

        val repoDto = repoDtoList.find { it.owner.login == userName && it.name == repoName } ?: RepoDto()

        return RepoDetailDto(
            repoDto.id,
            repoDto.name,
            OwnerDto(repoDto.owner.login, repoDto.owner.avatarUrl),
            starCount ?: repoDto.stargazersCount,
            0
        )
    }
}