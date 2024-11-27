package com.prac.local

import com.prac.local.datastore.token.TokenLocalDto

interface TokenLocalDataSource {
    fun getToken(): TokenLocalDto

    suspend fun setToken(token: TokenLocalDto)

    suspend fun clearToken()
}