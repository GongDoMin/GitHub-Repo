package com.prac.data.source.local

internal interface UserLocalDataSource {
    suspend fun setUserName(userName: String)

    suspend fun getUserName() : String

    suspend fun clearUserName()
}