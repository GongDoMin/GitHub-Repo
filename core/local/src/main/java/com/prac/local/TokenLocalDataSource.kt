package com.prac.local

import com.prac.local.model.TokenEntity

interface TokenLocalDataSource {
    fun getToken(): TokenEntity

    suspend fun setToken(token: TokenEntity)

    suspend fun clearToken()
}