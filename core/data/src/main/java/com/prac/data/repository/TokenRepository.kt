package com.prac.data.repository

interface TokenRepository {
    suspend fun authorizeOAuth(code: String): String

    suspend fun isLoggedIn() : Boolean

    suspend fun clearToken()
}