package com.prac.local.datastore.token

internal interface TokenDataStoreManager {
    suspend fun getToken(): TokenLocalDto

    suspend fun setToken(token: TokenLocalDto)

    suspend fun clearToken()
}