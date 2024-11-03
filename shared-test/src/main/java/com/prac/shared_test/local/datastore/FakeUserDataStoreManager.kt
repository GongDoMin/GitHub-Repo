package com.prac.shared_test.local.datastore

import com.prac.local.datastore.user.UserDataStoreManager

class FakeUserDataStoreManager(
    private var userName: String = ""
) : UserDataStoreManager {

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