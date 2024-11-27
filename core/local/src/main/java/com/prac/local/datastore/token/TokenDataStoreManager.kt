package com.prac.local.datastore.token

interface TokenDataStoreManager {
    suspend fun getToken(): TokenLocalDto

    suspend fun setToken(token: TokenLocalDto)

    suspend fun clearToken()
}