package com.prac.local.fake.source

import com.prac.local.UserLocalDataSource

class FakeUserLocalDataSource : UserLocalDataSource {

    private var userName = ""

    fun setInitialUserName() {
        userName = "test"
    }

    override suspend fun setUserName(userName: String) {
        this.userName = userName
    }

    override suspend fun getUserName(): String {
        return userName
    }

    override suspend fun clearUserName() {
        this.userName = ""
    }
}