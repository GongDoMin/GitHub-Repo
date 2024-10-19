package com.prac.data.source.local.datastore.token

internal interface TokenDataStoreManager {
    suspend fun saveTokenData(token: TokenLocalDto)

    suspend fun getToken(): TokenLocalDto

    suspend fun clearToken()
}