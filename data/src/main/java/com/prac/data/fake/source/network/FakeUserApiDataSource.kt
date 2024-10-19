package com.prac.data.fake.source.network

import com.prac.data.source.network.UserApiDataSource

class FakeUserApiDataSource : UserApiDataSource {
    override suspend fun getUserName(accessToken: String): String {
        return "test"
    }
}