package com.prac.shared_test.data

import com.prac.data.repository.UserRepository

class FakeUserRepository(
    private val apiUserName: String = ""
) : UserRepository {
    private var userName: String = ""

    override suspend fun getApiUserName(accessToken: String): String =
        apiUserName

    override suspend fun getLocalUserName(): String =
        userName

    override suspend fun setUserName(userName: String) {
        this.userName = userName
    }

    override suspend fun clearUserName() {
        userName = ""
    }

}