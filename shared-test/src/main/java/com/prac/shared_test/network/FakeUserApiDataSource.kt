package com.prac.shared_test.network

import com.prac.network.UserApiDataSource

class FakeUserApiDataSource(
    private val userName: String = ""
) : UserApiDataSource {

    override suspend fun getUserName(accessToken: String): String {
        return userName
    }
}