package com.prac.data.source.network

internal interface UserApiDataSource {
    suspend fun getUserName(accessToken: String) : String
}