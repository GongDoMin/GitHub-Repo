package com.prac.local.fake

import com.prac.local.datastore.user.UserDataStoreManager

class FakeUserDataStoreManager : UserDataStoreManager {

    private var userName = ""

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