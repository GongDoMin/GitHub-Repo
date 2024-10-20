package com.prac.local.datastore.user

internal interface UserDataStoreManager {
    suspend fun getUserName(): String

    suspend fun setUserName(userName: String)

    suspend fun clearUserName()
}