package com.prac.data.source.local.impl

import com.prac.data.source.local.UserLocalDataSource
import com.prac.data.source.local.datastore.user.UserDataStoreManager
import javax.inject.Inject

internal class UserLocalDataSourceImpl @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager
) : UserLocalDataSource {
    override suspend fun setUserName(userName: String) {
        userDataStoreManager.setUserName(userName)
    }

    override suspend fun getUserName(): String {
        return userDataStoreManager.getUserName()
    }

    override suspend fun clearUserName() {
        userDataStoreManager.clearUserName()
    }
}