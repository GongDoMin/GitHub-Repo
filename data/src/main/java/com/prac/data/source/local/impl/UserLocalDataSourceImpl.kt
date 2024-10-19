package com.prac.data.source.local.impl

import com.prac.data.source.local.UserLocalDataSource
import com.prac.data.source.local.datastore.user.UserDataStoreManager
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class UserLocalDataSourceImpl @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager
) : UserLocalDataSource {

    private var cachedUserName = ""

    init {
        runBlocking {
            cachedUserName = userDataStoreManager.getUserName()
        }
    }

    override suspend fun setUserName(userName: String) {
        userDataStoreManager.setUserName(userName)
        cachedUserName = userName
    }

    override suspend fun getUserName(): String {
        return cachedUserName
    }

    override suspend fun clearUserName() {
        userDataStoreManager.clearUserName()
        cachedUserName = ""
    }
}