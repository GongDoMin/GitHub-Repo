package com.prac.data.repository

interface UserRepository {
    suspend fun getApiUserName(accessToken: String) : String

    suspend fun getLocalUserName() : String

    suspend fun setUserName(userName: String)

    suspend fun clearUserName()
}