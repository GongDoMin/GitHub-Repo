package com.prac.data.repository

interface TokenRepository {
    suspend fun authorizeOAuth(code: String) : Result<Unit>

    suspend fun isLoggedIn() : Boolean

    suspend fun clearToken()

    suspend fun refreshToken(refreshToken: String) : Result<Unit>
}