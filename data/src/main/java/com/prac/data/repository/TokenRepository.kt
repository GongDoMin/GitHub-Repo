package com.prac.data.repository

interface TokenRepository {
    suspend fun authorizeOAuth(
        code: String
    ) : Result<Unit>

    suspend fun isLoggedIn() : Boolean

    suspend fun clearToken()

    /**
     * This methods is intended for internal use by the interceptor and should not be called from the ViewModel.
     */

    suspend fun refreshToken(refreshToken: String) : Result<Unit>

    fun getAccessToken() : String

    fun getRefreshToken() : String

    fun getAccessTokenIsExpired() : Boolean

    fun getRefreshTokenIsExpired() : Boolean
}