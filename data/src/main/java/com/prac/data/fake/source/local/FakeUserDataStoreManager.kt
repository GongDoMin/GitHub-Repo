package com.prac.data.fake.source.local

import com.prac.data.source.local.datastore.user.UserDataStoreManager

internal class FakeUserDataStoreManager : UserDataStoreManager {

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