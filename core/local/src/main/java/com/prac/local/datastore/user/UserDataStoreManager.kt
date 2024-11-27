package com.prac.local.datastore.user

interface UserDataStoreManager {
    suspend fun getUserName(): String

    suspend fun setUserName(userName: String)

    suspend fun clearUserName()
}