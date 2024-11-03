package com.prac.shared_test.local.source

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