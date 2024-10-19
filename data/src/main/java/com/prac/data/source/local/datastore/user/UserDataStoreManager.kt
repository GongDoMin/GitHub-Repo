package com.prac.data.source.local.datastore.user

internal interface UserDataStoreManager {
    suspend fun setUserName(userName: String)

    suspend fun getUserName(): String

    suspend fun clearUserName()
}