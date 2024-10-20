package com.prac.network.fake

import com.prac.network.UserApiDataSource

class FakeUserApiDataSource : UserApiDataSource {
    override suspend fun getUserName(accessToken: String): String {
        return "test"
    }
}