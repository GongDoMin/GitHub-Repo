package com.prac.network.fake

import com.prac.network.UserApiDataSource

class FakeUserApiDataSource : UserApiDataSource {

    private lateinit var throwable: Throwable

    fun setThrowable(throwable: Throwable) {
        this.throwable = throwable
    }

    override suspend fun getUserName(accessToken: String): String {
        if (::throwable.isInitialized) throw throwable

        return "test"
    }
}