package com.prac.local.datastore.token

import com.prac.local.model.TokenEntity

interface TokenDataStoreManager {
    suspend fun getToken(): TokenEntity

    suspend fun setToken(token: TokenEntity)

    suspend fun clearToken()
}