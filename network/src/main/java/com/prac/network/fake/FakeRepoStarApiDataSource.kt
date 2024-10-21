package com.prac.network.fake

import com.prac.network.RepoStarApiDataSource

class FakeRepoStarApiDataSource: RepoStarApiDataSource {

    private lateinit var throwable: Throwable

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun isStarred(userName: String, repoName: String) {
        if (::throwable.isInitialized) {
            throw throwable
        }
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        if (::throwable.isInitialized) {
            throw throwable
        }
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        if (::throwable.isInitialized) {
            throw throwable
        }
    }
}