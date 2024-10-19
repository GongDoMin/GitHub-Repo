package com.prac.data.source.local.datastore

internal interface UserDataStoreManager {
    suspend fun setUserName(userName: String)

    suspend fun getUserName(): String

    suspend fun clearUserName()
}