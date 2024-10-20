package com.prac.local

interface UserLocalDataSource {
    suspend fun setUserName(userName: String)

    suspend fun getUserName() : String

    suspend fun clearUserName()
}