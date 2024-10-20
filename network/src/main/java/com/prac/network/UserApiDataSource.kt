package com.prac.network

interface UserApiDataSource {
    suspend fun getUserName(accessToken: String) : String
}