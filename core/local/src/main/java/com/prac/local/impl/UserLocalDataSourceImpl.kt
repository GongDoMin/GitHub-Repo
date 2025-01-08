package com.prac.local.impl

import com.prac.local.UserLocalDataSource
import com.prac.local.datastore.user.UserDataStoreManager
import com.prac.local.model.TokenEntity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

internal class UserLocalDataSourceImpl @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager
) : UserLocalDataSource {

    private val cachedUserName: AtomicReference<String> = AtomicReference()

    init {
        runBlocking {
            cachedUserName.set(userDataStoreManager.getUserName())
        }
    }

    override suspend fun setUserName(userName: String) {
        userDataStoreManager.setUserName(userName)
        cachedUserName.set(userName)
    }

    override suspend fun getUserName(): String =
        cachedUserName.get()

    override suspend fun clearUserName() {
        userDataStoreManager.clearUserName()
        cachedUserName.set("")
    }
}