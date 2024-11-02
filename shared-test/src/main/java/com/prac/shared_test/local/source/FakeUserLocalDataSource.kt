package com.prac.shared_test.local.source

import com.prac.local.UserLocalDataSource

class FakeUserLocalDataSource(
    private var userName: String = ""
) : UserLocalDataSource {

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