package com.prac.shared_test.network

import com.prac.network.RepoStarApiDataSource

class FakeRepoStarApiDataSource(
    private val throwable: Throwable? = null
): RepoStarApiDataSource {

    override suspend fun isStarred(userName: String, repoName: String) {
        throwable?.let { throw it }
    }

    override suspend fun starRepository(userName: String, repoName: String) {
        throwable?.let { throw it }
    }

    override suspend fun unStarRepository(userName: String, repoName: String) {
        throwable?.let { throw it }
    }
}