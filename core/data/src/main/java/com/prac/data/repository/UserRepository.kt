package com.prac.data.repository

interface UserRepository {
    suspend fun getUserName(accessToken: String) : String

    suspend fun setUserName(userName: String)

    suspend fun clearUserName()
}